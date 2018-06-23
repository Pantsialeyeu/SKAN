package com.tkmi.test.unit

import com.google.gson.Gson
import com.tkmi.utils.DemoUtility
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import spock.lang.Specification

@AutoConfigureMockMvc
@SpringBootTest
class MvcService extends Specification{
	
	@Autowired MockMvc mockMvc
	
	def AGG_ENDPOINT = '/agg'
	
	def get(endpoint, body){
		def response = mockMvc.perform(MockMvcRequestBuilders.get(endpoint)
				.contentType('application/json; charset=UTF-8')
				.content(new Gson().toJson(body)))
				.andReturn().response
		new DemoUtility().byteArrayOutputStreamToMap(response.content)
	}

}
