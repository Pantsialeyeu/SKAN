package com.tkmi.test.integration.dataProvider

import org.testng.annotations.DataProvider

class DemoDataProvider {
	static json = """{
      "took" : 3106,
      "timed_out" : "false",
      "_shards" : {
        "total" : 5,
        "successful" : 5,
        "failed" : 0
      },
      "hits" : {
        "total" : 14375609,
        "max_score" : 0.0,
        "hits" : { }
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

  @DataProvider(parallel = true, indices = [])
  static Object[][] demo() {
    // json, key, expectedResult
    [
      [json, 'value', [5192015, 6647675, 932414, 1107660, 171793, 1498823]],
      [json, 'doc_count', [12848485, 3308947, 3140190, 168757, 1527124]],
      [json, '_doc_count', [[value:6647675], [value:5192015], [value:1107660], [value:932414], [value:171793], [value:1498823]]]
    ]
  }

}
