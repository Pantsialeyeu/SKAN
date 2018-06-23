package com.tkmi.test.unit.utils

import com.tkmi.utils.DemoUtility
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@SpringBootTest // if Spring's dependency injection is required for a test
class SpockDemoSpec extends Specification {
	
	@Autowired DemoUtility demoUtility // class autowired for demo only
  
  def setup(){
	  // when no @SpringBootTest annotation is used
    if (!demoUtility) new DemoUtility()
  }
  
	/* BDD Test
	given-when-then blocks
	verifyAll
	does not require assert
	direct verification (without intermediate variables, less code, clear test output)
	 */
	
    def 'getDuplicatedKeysTest'() {
      given: 'set of maps with and without duplicated keys'
      def map1 = ['a': 10, 'b': 10, 'c': 11, 'd': 12] // sample map
      def map2 = ['a': 10, 'b': 10, 'c': 11, 'd': 13] // duplicate
      and: 'maps have different sizes'
      def map3 = ['a': 10, 'b': 10] // duplicate
      def map4 = ['e': 10, 'f': 10] // non-duplicate
      def map5 = ['a': 40, 'b': 60, 'e': 10, 'f': 10] // partially duplicate

      when: 'getDuplicatedKeys method takes 2 maps'
      def result1 = demoUtility.getDuplicatedKeys(map1, map2)
      then: 'result is a list of duplicated keys match expected'
      verifyAll {
	      assert result1 == ['a', 'b', 'c', 'd']
	      demoUtility.getDuplicatedKeys(map1, map3) == ['a', 'b']
	      demoUtility.getDuplicatedKeys(map1, map4) == []
	      demoUtility.getDuplicatedKeys(map1, map5) == ['a', 'b']
      }
	    
      when: 'isDuplicatedKeys method takes 2 maps'
      then: 'result is boolean true or false and match expected'
      verifyAll {
        !demoUtility.isDuplicatedKeys(map1, map2)
        !demoUtility.isDuplicatedKeys(map1, map3)
        demoUtility.isDuplicatedKeys(map1, map4)
        !demoUtility.isDuplicatedKeys(map1, map5)
      }
    }
	
	/* Data-Driven Test
	@Unroll is mandatory for DDT tests
	expect-where blocks
	given-when-then-where blocks
	data table
	*/
	
	@Unroll 'getValueByKeyTest'() {
		
		expect: 'utility method getValueByKey produces expected result'
		verifyAll {
			demoUtility.getValueByKeyJson(jsonString, key) == expected
		}
		
		where: 'test data contains json string key to get its value and expected result'
		jsonString  | key          || expected
		json        | "value"      || [5192015, 6647675, 932414, 1107660, 171793, 1498823]
		json        | "doc_count"  || [12848485, 3308947, 3140190, 168757, 1527124]
		json        | "_doc_count" || [[value:6647675], [value:5192015], [value:1107660], [value:932414], [value:171793], [value:1498823]]
	}
	
	@Shared
	def json = """{
      "took" : 3106,
      "timed_out" : false,
      "_shards" : {
        "total" : 5,
        "successful" : 5,
        "failed" : 0
      },
      "hits" : {
        "total" : 14375609,
        "max_score" : 0.0,
        "hits" : [ ]
      },
      "aggregations" : {
        "Yes-volumePercentParent" : {
          "doc_count" : 12848485,
          "_doc_count" : {
            "value" : 5192015
          }
        },
        "_doc_count" : {
          "value" : 6647675
        },
        "director" : {
          "doc_count_error_upper_bound" : 0,
          "sum_other_doc_count" : 0,
          "buckets" : [ {
            "key" : "tim hall",
            "doc_count" : 3308947,
            "Yes-volumePercentParent" : {
              "doc_count" : 3140190,
              "_doc_count" : {
                "value" : 932414
              }
            },
            "_doc_count" : {
              "value" : 1107660
            },
            "!Yes-volumePercentParent" : {
              "doc_count" : 168757,
              "_doc_count" : {
                "value" : 171793
              }
            }
          } ]
        },
        "!Yes-volumePercentParent" : {
          "doc_count" : 1527124,
          "_doc_count" : {
            "value" : 1498823
          }
        }
      }
    }"""
}
