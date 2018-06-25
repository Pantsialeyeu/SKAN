package com.tkmi.test.unit.scanTest

import groovy.json.JsonBuilder
import spock.lang.Specification

class ScanTest extends Specification {
	
	def 'test1'(){
		given: 'area'
		def Xmin = 0
		def Ymin = 0
		def Xmax = 10
		def Ymax = 10
		and: 'initial position'
		def x = 0
		def y = 0
		and: 'expected coverage'
		def expectedCoverage = []
		(Ymin..Ymax).each{ y_pos ->
			expectedCoverage.add('y' + y_pos)
			(Xmin..Xmax).each{ x_pos ->
				expectedCoverage.add('x' + x_pos)
			}
		}
		when: 'execution'
		def actualCoverage = []
		while (!(y > Ymax)) {
			println 'y' + y
			actualCoverage.add('y' + y)
			x = 0
			println 'x' + x
			actualCoverage.add('x' + x)
			while (!(x == Xmax)) {
				x++
				println 'x' + x
				actualCoverage.add('x' + x)
			}
			y++
		}
		then: 'final position should match expected'
		assert x == Xmax
		assert y == Ymax + 1
		and: 'actual coverage should match expected coverage'
		assert actualCoverage == expectedCoverage
	}
	
	def 'test2'(){
		given: 'area'
		def Xmin = 0
		def Ymin = 0
		def Xmax = 10
		def Ymax = 10
		and: 'initial position'
		def x = 0
		def y = 0
		and: 'expected coverage'
		def expectedCoverage = []
		(Ymin..Ymax).each{ y_pos ->
			expectedCoverage.add('y' + y_pos)
			if (y_pos in [0, 2, 4, 6, 8, 10]) {
				(Xmin..Xmax).each{ x_pos ->
					expectedCoverage.add('x' + x_pos)
				}
			} else {
				(Xmax..Xmin).each{ x_pos ->
					expectedCoverage.add('x' + x_pos)
				}
			}
		}
		when: 'execution'
		def actualCoverage = []
		while (!(y > Ymax)) {
			println 'y' + y
			actualCoverage.add('y' + y)
			if (x == Xmax) {
				println 'x' + x
				actualCoverage.add('x' + x)
				while (!(x == Xmin)) {
					x--
					println 'x' + x
					actualCoverage.add('x' + x)
				}
			} else {
				println 'x' + x
				actualCoverage.add('x' + x)
				while (!(x == Xmax)) {
					x++
					println 'x' + x
					actualCoverage.add('x' + x)
				}
			}
			y++
		}
	
		then: 'final position should match expected'
		assert x == Xmax
		assert y == Ymax + 1
		and: 'actual coverage should match expected coverage'
		assert actualCoverage == expectedCoverage
	}
	
	def 'test3'(){
		given: 'area'
		def Xmin = 0
		def Ymin = 0
		def Xmax = 10
		def Ymax = 10
		and: 'initial position'
		def x = 0
		def y = 0
		and: 'expected coverage'
		def expectedCoverage = []
		(Ymin..Ymax).each{ y_pos ->
			expectedCoverage.add('y' + y_pos)
			(Xmin..Xmax).each{ x_pos ->
				expectedCoverage.add('x' + x_pos)
			}
		}
		when: 'execution'
		def actualCoverage = []
		while (!(y > Ymax)) {
			actualCoverage.add('y' + y)
			x = 0
			actualCoverage.add('x' + x)

			while (!(x == Xmax)) {
				
				// sensor block
				def value = getU()
				
				// Workstation block
				sendToPc(x, y, value)

				x++
				
				actualCoverage.add('x' + x)
			}
			y++
		}
		then: 'final position should match expected'
		assert x == Xmax
		assert y == Ymax + 1
		and: 'actual coverage should match expected coverage'
		assert actualCoverage == expectedCoverage
		println new JsonBuilder().toPrettyString(output)
	}
	
	// Sensor
	
	def getU(){
		new Random().nextDouble() // simulates U measurement
	}
	
	// Workstation integration
	
	def counter = 0
	def output = []
	
	def sendToPc(x, y, u){
		output.add(["$counter": ['x': x, 'y': y, 'u': getU()]])
		counter++
	}
}
