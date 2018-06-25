package com.tkmi.test.unit.scanTest

import groovy.json.JsonBuilder
import spock.lang.Specification

class ScanSensorTest extends Specification {
	
	def 'scanning management system demo test'(){
		given: 'scanning area:'
		and: 'scanner initial position;'
		and: 'scanner final position.'
		def Xstart = 0
		def Ystart = 0
		def Xend = 10
		def Yend = 10
		and: 'expected coverage'
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
		assert result.counter == 110
		and: 'print out PC results'
		print()

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
