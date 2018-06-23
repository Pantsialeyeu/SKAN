package com.tkmi.test.unit.controllers

import com.tkmi.test.unit.MvcService
import com.tkmi.test.unit.dataProvider.DemoDataProvider

class DemoControllerTest extends MvcService {
	
	def paramGreetingShouldReturnTailoredMessage() {
		given:
		def contentString = [
				message: data[0],
				key: data[1]
		]
		when:
		def res = get(AGG_ENDPOINT, contentString)
		then:
		assert res.data == data[2]
		where: data << DemoDataProvider.demo()
	}
}
