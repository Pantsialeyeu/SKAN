package com.tkmi.test.unit.scanTest

import groovy.json.JsonBuilder
import spock.lang.Specification
import spock.lang.Unroll


class ScanSensorTest2 extends Specification {
	
	@Unroll
	def 'scanning management system demo test 2'(){
	
		setup: 'area size start and end parameters should be positive integers'
		def Xmin = 0
		def Ymin = 0
		def Xmax = 2000
		def Ymax = 2000
		
		and: 'precission'
		precission = precission ? (precission * 10).toInteger() : 10
		assert precission >= 1 && precission <= 50
		
		def Xcounter = 1
		def Ycounter = 1
		
		if (area in ['S', 'M', 'L']) {
			def delta = area == 'S' ? 1000 : area == 'M' ? 1500 : 2000
			Xstart = Xmax - delta
			Ystart = Ymax - delta
			Xend = Xstart
			while (Xend + precission <= Xmax) {
				Xend += precission
				Xcounter++
			}
			Yend = Ystart
			while (Yend + precission <= Ymax) {
				Yend += precission
				Ycounter++
			}
		} else {
			Xstart = Xstart * 10
			Xend = Xend * 10
			def Xexp = Xstart
			while (Xexp + precission <= Xmax && Xexp + precission <= Xend) {
				Xexp = Xexp + precission
				Xcounter++
			}
			Xend = Xexp
			Ystart = Ystart * 10
			Yend = Yend * 10
			def Yexp = Ystart
			while (Yexp + precission <= Ymax && Yexp + precission <= Yend) {
				Yexp = Yexp + precission
				Ycounter++
			}
			Yend = Yexp
		}
		
		total_count = Ycounter * Xcounter
		
	
		assert Xstart >= Xmin && Xend >= Xstart && Xend <= Xmax && (Xend - Xstart) <= (Xmax - Xmin)
		assert Ystart >= Ymin && Yend >= Ystart && Yend <= Ymax && (Yend - Ystart) <= (Ymax - Ymin)
	
		and: 'monitoring'
		
		def monitorOption = monitoringOption ? monitoringOption : 1
		
		when: 'scan execution'
		def result = scan(Xstart, Xend, Ystart, Yend, precission, monitorOption)
		then: 'final position should match expected'
		assert result.x == Xend/10 || result.x == Xstart/10
		assert result.y == Ystart/10 + (Ycounter * (precission/10))
		where: 'Parameters: Scanning area (mm), Precission (mm), Monitoring option'
		area  | Xstart  | Ystart  | Xend  | Yend  | precission  | monitoringOption
//		'S'   | null    | null    | null  | null  | null        | 1
		'M'   | null    | null    | null  | null  | 0.1         | 2
//		'L'   | null    | null    | null  | null  | 5           | 3
//		null  | 0       | 0       | 10    | 10    | 1           | null
//		null  | 0       | 0       | 5     | 5     | 1.1         | null
//		null  | 5       | 5       | 10    | 10    | 1.2         | null
	}
	
	// Scanner
	
	def scan(Xstart, Xend, Ystart, Yend, precission, monitoringOption){
		curTime = System.currentTimeMillis()
		def x = Xstart,
				y = Ystart
		while (!(y > Yend)) {
			if (x != Xstart) {
				while (!(x == Xstart)) {
					collectResults(x, y, monitoringOption)
					x -= precission
				}
				collectResults(x, y, monitoringOption)
				
			} else {
				while (!(x == Xend)) {
					collectResults(x, y, monitoringOption)
					x += precission
				}
				collectResults(x, y, monitoringOption)
			}
			if (monitoringOption == 2) {
				sendToPC()
			}
			y += precission
		}
		if (monitoringOption == 3) {
			sendToPC()
		}
		[x: (x/10), y:(y/10)]
	}
	
	// Sensor integration
	
	def getU(){
		sleep(2)
		new Random().nextDouble() // simulates U measurement
	}
	
	// Workstation integration
	
	def workstation = [:]
	def counter = 0
	
	def collectResults(x, y, monitoringOption){
		counter++
		def startTime = System.currentTimeMillis()
		// sensor block
		def value = getU()
		
		workstation << [
			"$counter": [
				'x': x/10,
				'y': y/10,
				'u': value,
				'took_ms': System.currentTimeMillis() - startTime
			]
		]
		if (monitoringOption == 1) {
			sendToPC()
		}
	}
	
	def curTime
	def total_count
	def remained_count
	def avg = []
	def elapsedTotalTime = 0
	
	// print out PC results
	def sendToPC(){
		def times = []
		workstation.each { entry ->
			times.add(entry.value.took_ms)
		}
		
		remained_count = remained_count ? remained_count : total_count
		def elapsedTime = times.sum()
		elapsedTotalTime += elapsedTime
		def avgTime = elapsedTime / times.size()
		avg.add(avgTime)
		workstation << [
				'stats': [
						elapsed_time_ms: elapsedTime,
						elapsed_total_time: elapsedTotalTime,
						avg_time_ms: avgTime,
						estimated_remaining_time_ms: remained_count * (avg.sum() / avg.size())]
		]
		
		def timestamp = new Date().format("yyyyMMddHHmmssSSS", TimeZone.getTimeZone('UTC'))
		curTime = System.currentTimeMillis()
		new File("scan_$timestamp" + '.json').write(new JsonBuilder(workstation).toPrettyString())
		
		workstation = [:]
		remained_count = remained_count ? remained_count - times.size() : total_count - times.size()
		true
	}
	
}
