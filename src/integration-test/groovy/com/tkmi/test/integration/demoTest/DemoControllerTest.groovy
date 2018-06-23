package com.tkmi.test.integration.demoTest

import com.tkmi.test.integration.BaseTest
import com.tkmi.test.integration.dataProvider.DemoDataProvider
import org.testng.annotations.Test

class DemoControllerTest extends BaseTest {
	
	@Test(dataProvider = 'demo', dataProviderClass = DemoDataProvider)
	void 'Demo test'(message, key, expectedResult){
		// todo given
		def payload =
				[
						message: message,
						key: key
				]
		// todo when
		def res = restService.restAgg(payload)
		// todo then
		assert res.data.data == expectedResult
	}
}
