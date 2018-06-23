package com.tkmi.test.unit.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.google.gson.Gson
import com.tkmi.services.DemoService
import com.tkmi.test.unit.dataProvider.DemoDataProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Unroll

import static org.mockito.Mockito.when

@WebMvcTest
class DemoMvcMockNoAppContextTest extends Specification {
	
	@Autowired MockMvc mockMvc
	
	@MockBean DemoService demoService
	
	ObjectMapper mapper = new ObjectMapper()

    @Ignore('wip')
	@Unroll 'Strategy_1_MockMVC_in_Standalone_Mode_Test'() {
		given: 'request body'
		def requestProperties = [
				message: data[0],
				key: data[1]
		]
		when: 'controller under test'
		and: 'mocked service'
		when(demoService.agg())
				.thenReturn(new ResponseEntity<>((ObjectNode) mapper.createObjectNode()
				.set("data", mapper.valueToTree(data[2])), HttpStatus.OK))
		def response = mockMvc.perform(MockMvcRequestBuilders.get('/agg')
				.contentType('application/json; charset=UTF-8')
				.content(new Gson().toJson(requestProperties)))
				.andReturn().response
		
		then: 'so we’re not loading any application context'
		assert response.content.toString() != ''
		assert response.content.toString().size() > 10
		where: data << DemoDataProvider.demo()
	}
}
