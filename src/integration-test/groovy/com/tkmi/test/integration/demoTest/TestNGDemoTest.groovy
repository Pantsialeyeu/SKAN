package com.tkmi.test.integration.demoTest

import com.tkmi.test.integration.BaseTest
import com.tkmi.test.integration.service.Groups
import com.tkmi.utils.DemoUtility
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

class TestNGDemoTest extends BaseTest {

  DemoUtility demoUtility = new DemoUtility()

  def payload, payload2, actual, actual2, aggs, aggsExp
	
  @BeforeMethod void setup() {
    aggsExp = [
        'agent_rating_BOTTOM_BOX_COUNT',
        'agent_rating_BOTTOM_BOX_COUNT.aggs',
        'agent_rating_BOTTOM_BOX_COUNT.aggs.exclude_filter',
        'agent_rating_BOTTOM_BOX_COUNT.aggs.exclude_filter.filter',
        'agent_rating_BOTTOM_BOX_COUNT.aggs.exclude_filter.filter.not',
        'agent_rating_BOTTOM_BOX_COUNT.aggs.exclude_filter.filter.not.or',
        'agent_rating_BOTTOM_BOX_COUNT.aggs.exclude_filter.filter.not.or.terms',
        'agent_rating_BOTTOM_BOX_COUNT.aggs.exclude_filter.filter.not.or.terms.agent_rating',
        'agent_rating_BOTTOM_BOX_COUNT.aggs.exclude_filter.filter.not.or.terms.agent_rating.1',
        'agent_rating_BOTTOM_BOX_COUNT.aggs.exclude_filter.filter.not.or.terms.agent_rating.3',
        'agent_rating_BOTTOM_BOX_COUNT.aggs.exclude_filter.filter.not.or.terms.agent_rating.4',
        'agent_rating_BOTTOM_BOX_COUNT.filter',
        'agent_rating_BOTTOM_BOX_COUNT.filter.range',
        'agent_rating_BOTTOM_BOX_COUNT.filter.range.agent_rating',
        'agent_rating_BOTTOM_BOX_COUNT.filter.range.agent_rating.lte'
    ]

    aggs = [
        agent_rating_BOTTOM_BOX_COUNT: [
            aggs  : [
                exclude_filter: [
                    filter: [
                        not: [
                            or: [
                                [
                                    terms: [
                                        agent_rating: [
                                            1,
                                            3,
                                            4
                                        ]
                                    ]
                                ]
                            ]
                        ]
                    ]
                ]
            ],
            filter: [
                range: [
                    agent_rating: [
                        lte: 3.0
                    ]
                ]
            ]
        ]
    ]
    payload = [
        attr: [
            [
                name: '_language'
            ],
            [
                type: 'bb',
                name: 'num_agent_rating',
                nullInclude: false,
                bottomThreshold: 4
            ]
        ],
        debug: true,
        filter: [
            date: [
                startDate: 20150404,
                endDate: 20150405
            ]
        ],
        projectId: 1,
        systemSchema: [
            user: 'ss_testshm',
            password: 'ss_testshm',
            url: 'jdbc: postgresql: //10.153.75.47: 5432/postgres',
            serverName: 'dev02',
            databaseName: 'dev_db',
            portNumber: 5432,
            description: 'Systemschema'
        ],
        source: [
            cluster: [
                hosts: [
                    [
                        http: [
                            hostName: 'lanl02',
                            hostAddress: '10.153.75.47',
                            port: 9200
                        ],
                        transport: [
                            hostName: 'lanl02',
                            hostAddress: '10.153.75.47',
                            port: 9300
                        ]
                    ]
                ],
                description: 'devcluster',
                name: 'dev02_cls'
            ],
            index: [
                readAlias: 'read_1$4036746',
                writeAlias: 'write_1$4036746',
                dataIndexName: '1$4036746',
                percolatorIndexName: '1$p$4036746'
            ]
        ]
    ]

    actual = [
        'attr',
        'attr.name',
        'attr.type',
        'attr.name',
        'attr.nullInclude',
        'attr.bottomThreshold',
        'debug',
        'filter',
        'filter.date',
        'filter.date.startDate',
        'filter.date.endDate',
        'projectId',
        'systemSchema',
        'systemSchema.user',
        'systemSchema.password',
        'systemSchema.url',
        'systemSchema.serverName',
        'systemSchema.databaseName',
        'systemSchema.portNumber',
        'systemSchema.description',
        'source',
        'source.cluster',
        'source.cluster.hosts',
        'source.cluster.hosts.http',
        'source.cluster.hosts.http.hostName',
        'source.cluster.hosts.http.hostAddress',
        'source.cluster.hosts.http.port',
        'source.cluster.hosts.transport',
        'source.cluster.hosts.transport.hostName',
        'source.cluster.hosts.transport.hostAddress',
        'source.cluster.hosts.transport.port',
        'source.cluster.description',
        'source.cluster.name',
        'source.index',
        'source.index.readAlias',
        'source.index.writeAlias',
        'source.index.dataIndexName',
        'source.index.percolatorIndexName'
    ]

    payload2 = [
        source: [
            cluster: [
                hosts: [
                    [
                        http: [
                            hostName: 'lanl02',
                            hostAddress: '10.153.75.47',
                            port: 9200
                        ],
                        transport: [
                            hostName: 'lanl02',
                            hostAddress: '10.153.75.47',
                            port: 9300
                        ]
                    ]
                ],
                description: 'devcluster',
                name: 'dev02_cls'
            ]
        ]
    ]

    actual2 = [
        'source',
        'source.cluster',
        'source.cluster.hosts',
        'source.cluster.hosts.http',
        'source.cluster.hosts.http.hostName',
        'source.cluster.hosts.http.hostAddress',
        'source.cluster.hosts.http.port',
        'source.cluster.hosts.transport',
        'source.cluster.hosts.transport.hostName',
        'source.cluster.hosts.transport.hostAddress',
        'source.cluster.hosts.transport.port',
        'source.cluster.description',
        'source.cluster.name'
    ]
  }

    @Test(groups = Groups.NON_PARALLEL)
    void 'getDuplicatedKeysTest'() {
        def map1 = ['a': 10, 'b': 10, 'c': 11, 'd': 12] // sample map
        def map2 = ['a': 10, 'b': 10, 'c': 11, 'd': 13] // duplicate
        def map3 = ['a': 10, 'b': 10] // duplicate
        def map4 = ['e': 10, 'f': 10] // non-duplicate
        def map5 = ['a': 4, 'b': 6, 'e': 10, 'f': 10] // partially duplicate
        assert demoUtility.getDuplicatedKeys(map1, map2) == ['a', 'b', 'c', 'd']
        assert demoUtility.getDuplicatedKeys(map1, map3) == ['a', 'b']
        assert demoUtility.getDuplicatedKeys(map1, map4) == []
        assert demoUtility.getDuplicatedKeys(map1, map5) == ['a', 'b']
        assert !demoUtility.isDuplicatedKeys(map1, map2)
        assert !demoUtility.isDuplicatedKeys(map1, map3)
        assert demoUtility.isDuplicatedKeys(map1, map4)
        assert !demoUtility.isDuplicatedKeys(map1, map5)
    }

    @Test void 'getNestedMapKeysTest'(){
        assert demoUtility.getNestedMapKeys(payload) == actual
        assert demoUtility.getNestedMapKeys(payload2) == actual2
        assert demoUtility.getNestedMapKeys(aggs) == aggsExp
    }

    @Test void 'getPathKeysByKeyTest'(){
        assert demoUtility.getPathKeysByKey(payload2, "source") == ['source']
        assert demoUtility.getPathKeysByKey(payload2, "cluster") == ['source.cluster']
        assert demoUtility.getPathKeysByKey(payload2, "transport") == ['source.cluster.hosts.transport']
        assert demoUtility.getPathKeysByKey(payload2, "port") == ['source.cluster.hosts.http.port', 'source.cluster.hosts.transport.port']
    }

    @Test void 'collectMapsTest'(){
        def map = [
            OrganizationName: 'SampleTest',
            Address: [
                Street: '123 Sample St',
                PostalCode: '00000',
            ],
            Address2: [
                Street: '456 Sample St',
                PostalCodeS: [CODE1: '00001', CODE2: '00002']
            ],
        ]
        def assertMap = [
            OrganizationName: 'SampleTest',
            Address: [
                Street: '123 Sample St',
                PostalCode: '00000',
            ],
            Address2: [
                Street: '456 Sample St',
                PostalCodeS: [CODE1: '00001', CODE2: '00002']
            ],]
        assert assertMap == demoUtility.collectMaps(map)[0]
    }

    @Test void 'getValueByKeyTest'(){
        assert demoUtility.getValueByKey(payload2, "port") == [9200, 9300]
    }

    @Test void 'getKeyValuePairsByKeyTest'(){
        assert demoUtility.getKeyValuePairsByKey(payload2, 'port').toString() == [[port:9200], [port:9300]].toString()
    }

    @Test void 'getBucketByKeyTest'(){
        assert demoUtility.getBucketByKey(payload2, "port").toString() == [[hostName:'lanl02', hostAddress:'10.153.75.47', port:9200], [hostName:'lanl02', hostAddress:'10.153.75.47', port:9300]].toString()
    }

    @Test void 'getValueByPathKeysTest'(){
        assert demoUtility.getValueByPathKeys(payload2, 'source.cluster.hosts.http.port') == [9200]
        assert demoUtility.getValueByPathKeys(payload2, demoUtility.getPathKeysByKey(payload2, 'port')) == [9200, 9300]
    }

    @Test void 'getPathKeysValuePairsTest'(){
        def res = demoUtility.getPathKeysValuePairs(payload2, 'port')
        def map = [:]
        map.put('source.cluster.hosts.http.port', 9200)
        map.put('source.cluster.hosts.transport.port', 9300)
        assert res.toString() == map.toString()
        res = demoUtility.getPathKeysValuePairs(payload2)
        assert res.keySet().toString() == actual2.toString()
        res.values.each { assert it != null }
    }
}
