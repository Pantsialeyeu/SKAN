package com.tkmi.test.unit.scanTest

import groovy.json.JsonBuilder
import spock.lang.Specification
import spock.lang.Unroll

class ScanSensorTest extends Specification {
	
	@Unroll
	def 'scanning management system demo test'(){
		setup: 'area size start and end parameters should be positive integers'
		assert Xstart.class.is(Integer) && Xend.class.is(Integer) && Ystart.class.is(Integer) && Yend.class.is(Integer)
		assert Xstart >= 0 && Xend >= 0 && Ystart >= 0 && Yend >= 0
		and:  'expected coverage'
		def expectedCoverage = []
		(Ystart..Yend).each{ y_pos ->
			expectedCoverage.add('y' + y_pos)
			(Xstart..Xend).each{ x_pos ->
				expectedCoverage.add('x' + x_pos)
			}
		}
		when: 'scan execution'
		def result = scan(Xstart, Xend, Ystart, Yend)
		then: 'final position should match expected'
		assert result.x == Xend
		assert result.y == Yend + 1
		and: 'actual coverage should match expected coverage'
		assert result.coverage == expectedCoverage
		assert result.counter == (Xend - Xstart) * ((Yend - Ystart) + 1)
		and: 'print out PC results'
		print()
		where: 'scanning area:'
		Xstart  | Ystart  | Xend  | Yend
		0       | 0       | 10    | 10
		0       | 0       | 5     | 5
		5       | 5       | 10    | 10

	}
	
	// Scanner
	
	def scan(Xstart, Xend, Ystart, Yend){
		def x = Xstart,
				y = Ystart,
				coverage = [],
				counter = 0
		while (!(y > Yend)) {
			coverage.add('y' + y)
			x = Xstart
			coverage.add('x' + x)
			
			while (!(x == Xend)) {
				
				counter++
				
				// sensor block
				def value = getU()
				
				// Workstation block
				sendToPc(x, y, value, counter)
				
				x++
				
				coverage.add('x' + x)
			}
			y++
		}
		[x: x, y:y, coverage: coverage, counter: counter]
	}
	
	// Sensor integration
	
	def getU(){
		new Random().nextDouble() // simulates U measurement
	}
	
	// Workstation integration
	
	def workstation = [:]
	
	def sendToPc(x, y, u, count){
		workstation << [
				"$count":
						[
								'x': x,
								'y': y,
								'u': u
						]
		]
	}
	
	def print(){
		def timestamp = new Date().format("yyyyMMddHHmmssSSS", TimeZone.getTimeZone('UTC'))
		new File("scan_$timestamp" + '.json').write(new JsonBuilder(workstation).toPrettyString())
		true
	}
	
}
