package com.tkmi.utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.google.gson.Gson
import groovy.json.JsonSlurper
import org.springframework.stereotype.Component

@Component
class DemoUtility implements Serializable {
	
	def serialVersionUID = 1L

  List<String> getNestedMapKeys(map, String keyPrefix = '', increment=true) {
    def result = []
    map.each {
      def top = it
      if (top.value instanceof Map) {
        String k = increment ? keyPrefix += "$top.key" : keyPrefix + "$top.key"
        result += k
        result += getNestedMapKeys(top.value, "$k.", top.value.size() > 1 ? false : true)
        keyPrefix = increment ? '' : keyPrefix
      }
      else if (top.value instanceof List) {
        String k = keyPrefix + "$top.key"
        result += k
        top.value.each {
          result += getNestedMapKeys(it, "$k.", false)
        }
      }
      else {
        if (!top.getClass().equals(LinkedHashMap$Entry)){
          result << "$keyPrefix$top"
        }
        else {
          result << "$keyPrefix$top.key"
        }
      }
    }
    result
  }

  List getPathKeysByKey(Map map, String key){
    def mapKeys = getNestedMapKeys(map)
    def result = []
    mapKeys.each {
      def split = it.tokenize( '.' )
      !split[-1].equals(key) ?: result << it
    }
    result
  }

  List getPathKeysIgnoreKey(Map map, String ignoreKey = null){
    def mapKeys = getNestedMapKeys(map)
    def result = []
    def out = []
    mapKeys.each {
      def split = it.tokenize( '.' )
      if (ignoreKey) {
        split[-1].tokenize( '=' )[0].equals(ignoreKey) ?: result << it
      } else {
        result << it
      }
    }
    result.each {
      out += it.tokenize( '=' )[0]
    }
    out
  }

  List getPathKeysIgnoreKeys(Map map, List ignoreKey){
    def mapKeys = getNestedMapKeys(map)
    def result = []
    def out = []
    mapKeys.each {
      def split = it.tokenize( '.' )
      if (ignoreKey) {
        ignoreKey.contains(split[-1].tokenize( '=' )[0]) ?: result << it
      } else {
        result << it
      }
    }
    result.each {
      out += it.tokenize( '=' )[0]
    }
    out.unique()
  }

  def getValueByKeyJson(json, key){
    getValueByKey(new Gson().fromJson(json.toString(), Map.class), key)
  }
  
  def getValueByKey(map, key){
    map.class == String.class ?
      getValueByKeyJson(map, key) :
      collectMaps(map).findAll{key in it.keySet()}[key]
  }

  def getBucketByKey(map, key){
    map = map.getClass().equals(String) ? new JsonSlurper().parseText(map) : map
    collectMaps(map).findAll{key in it.keySet()}
  }

  def getKeyValuePairsByKey(map, key){
    def result = []
    collectMaps(map).findAll{key in it.keySet()}[key].each{
      result.add(["$key": it])
    }
    result
  }

  def getValueByPathKeys(map, pathKeys){
    def result = []
    pathKeys.getClass().equals(String) ?
      pathKeys.tokenize('.').each {
        map = getValueByKey(map, it)
        result = map ? map : result
      } :
      pathKeys.each { result += getValueByPathKeys(map, it.toString()) }
    result
  }

  def getPathKeysValuePairs(map, key=false){
    def pathKeys = key ? getPathKeysByKey(map, key) : getNestedMapKeys(map)
    def values = getValueByPathKeys(map, pathKeys)
    [pathKeys, values].transpose().inject([:]) { result, it -> result << ( it as MapEntry ) }
  }

  def collectMaps(e) {
    e.with{
      if (it instanceof Map) {
        [it] + it.values().collect{ it != null ? collectMaps(it) : [] }
      } else if (it instanceof Collection) {
        it.collect{ collectMaps(it) }
      } else {
        []
      }
    }.flatten()
  }

  static ArrayList getDuplicatedKeys(map1, map2) {
    ObjectMapper mapper = new ObjectMapper()
    map1 instanceof ObjectNode ? map1 = mapper.convertValue(map1, Map.class) : map1
    map2 instanceof ObjectNode ? map2 = mapper.convertValue(map2, Map.class) : map2
    map1.keySet().intersect(map2.keySet())
  }

  static isDuplicatedKeys(map1, map2) {
    getDuplicatedKeys(map1, map2).size() == 0
  }

  def toJson(map, depth = 0) {
    def json = ""
    depth.times { json += "\t" }
    json += "{"
    map = new ObjectMapper().convertValue(map, Map.class)
    map.each { key, value ->
      json += "\"$key\":"
      json += jsonValue(value, depth)
      json += ", "
    }
    json = (map.size() > 0) ? json.substring(0, json.length() - 2) : json
    json += "}"
    json
  }

  private jsonValue(element, depth) {
    if (element instanceof Map) {
      return "\n" + toJson(element, depth + 1)
    }
    if (element instanceof List) {
      def list = "["
      element.each { elementFromList ->
        list += jsonValue(elementFromList, depth)
        list += ", "
      }
      list = (element.size() > 0) ? list.substring(0, list.length() - 2) : list
      list += "]"
      return list
    }
    (element instanceof String) ? "\"$element\"": element?.toString()
  }
  
  def byteArrayOutputStreamToMap(bytes){
    new ObjectMapper().readValue(bytes.toString(), LinkedHashMap)
  }
}