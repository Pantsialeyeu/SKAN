package com.tkmi.test.unit.utils


import com.fasterxml.jackson.databind.node.ObjectNode
import spock.lang.Specification

import static com.tkmi.utils.JsonHelper.*

class JsonHelperTest extends Specification {

  def testDoc = """
    {
      "version": 123,
      "attr": [
        {
          "name": "director",
          "size": 5,
          "nullInclude": true
        },
        {
          "name": "lob",
          "size": 5,
          "nullInclude": true
        }
      ],
      "metrics": [
        {
          "name": "num_agent_rating"
        }
      ],
      "debug": 0,
      "filter": {
        "date": {
          "startDate": 20130101,
          "endDate": 20131231
        }
      },
      "source": {
        "index": {
          "readAlias": "4036746"
        },
        "cluster": {
          "name": "dev02_cls",
          "hosts": [
            {
              "http": {
                "hostName": "dev02"
              }
            }
          ]
        }
      }
    }"""

  ObjectNode doc = (ObjectNode) parseString(testDoc)

  def 'getNodeTest'() {
    expect:
    verifyAll {
	    assert getNode(doc, '.version').asInt() == 123
	    assert getNode(doc, '.attr[0].name').asText() == 'director'
    }
  }

  def 'createObjectNodeTest'() {
    when:
      def node = createObjectNode();
    then:
    verifyAll {
	    assert node != null
	    assert node instanceof ObjectNode
	    assert node.toString() == '{}'
    }
  }

  def 'putNodeObjectTest'() {
    when:
      def node = createObjectNode()
      putNodeObject(node, "some string", "a", "b", "c", "string")
      putNodeObject(node, Double.valueOf(Math.PI), "a", "b", "c", "double")
      putNodeObject(node, Long.valueOf(42), "a", "b", "c", "long")
      def childNode = parseString('{"blue":"sky"}')
      putNodeObject(node, childNode, "a", "b", "c", "jsonnode")
      putNodeObject(node, null, "a", "b", "c", "nullvalue")
    then:
      getNode(node, ".a.b.c.string").asText() == "some string"
      getNode(node, ".a.b.c.double").asDouble() == Math.PI
      getNode(node, ".a.b.c.long").asLong() == 42
      getNode(node, ".a.b.c.jsonnode").toString() == '{"blue":"sky"}'
      getNode(node, ".a.b.c.nullvalue").isNull()
  }

  def 'parseStringTest'() {
    when:
      def t = parseString(testDoc)
    then:
      assert getNode(t, ".version").asInt() == 123
      assert getNode(t, ".attr[0].name").asText() == "director"
      assert getNode(t, ".attr[0].size").asText() == "5"
      assert getNode(t, ".attr[1].nullInclude").asBoolean()
      assert getNode(t, ".source.cluster.hosts[0].http.hostName").asText() == "dev02"
    
    when: 'parsing embedded quotes'
      def p = [
          '''With double quotes: 3\", or \"foo\", etc.''',
          '''A \\slashy\\ string''',
          '''With carriage\rreturn''',
          '''With new\nline''',
          '''With \ttab''',
          '''Unicode: currencies (\u20AC, \u00A5, \u00A2, \u00A3"''',
          '''Unicode: diacritics (\u00C5, \u00C8, \u00D8, \u00DC"'''
      ]
    then:
    // Check test strings, appending outcomes to array for subsequent assertion
    def bucketOutcomes =  p.collect { s ->
      try {
        def q = parseString('''{"key": "''' + escapeJsonStringValue(s) + '''"}''')
      } catch (Exception e) {
        return e.toString()
      }
      return true
    }
    then:
    // Check all items in outcome are boolean true (no issues found)
    // Using this array comparison allows assertion outputs to clearly identify issues per test string
    bucketOutcomes == (1..p.size()).collect { true }
  }

  def 'writeAsStringTest'() {
    when:
      def node = createObjectNode()
      putNodeObject(node, "some string", "a", "b", "c", "string")
    then:
      assert writeAsString(node) == '{"a":{"b":{"c":{"string":"some string"}}}}'
  }
}
