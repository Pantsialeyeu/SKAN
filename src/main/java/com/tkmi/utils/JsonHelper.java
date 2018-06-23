package com.tkmi.utils;

import static com.tkmi.utils.Streams.toStream;
import static org.apache.commons.collections4.IteratorUtils.toList;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.*;
import com.fasterxml.jackson.databind.type.MapType;
import net.thisptr.jackson.jq.JsonQuery;
import net.thisptr.jackson.jq.exception.JsonQueryException;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


public class JsonHelper {
  private static ObjectMapper mapper = new ObjectMapper();

  public static ObjectNode createObjectNode() {
    return mapper.createObjectNode();
  }

  public static ObjectNode createObjectNode(String name, String value) {
    return createObjectNode().put(name, value);
  }

  public static ObjectNode createObjectNode(String name, Integer value) {
    return createObjectNode().put(name, value);
  }

  public static ObjectNode createObjectNode(String name, Long value) {
    return createObjectNode().put(name, value);
  }

  public static ObjectNode createObjectNode(String name, Boolean value) {
    return createObjectNode().put(name, value);
  }

  public static ObjectNode createObjectNode(String name, JsonNode value) {
    return (ObjectNode) createObjectNode().set(name, value);
  }

  public static ArrayNode createArrayNode() {
    return mapper.createArrayNode();
  }

  public static ObjectNode putNodeObject(JsonNode origNode, Object value, String... pathKeys) {
    JsonNode currNode = origNode;
    for (int i = 0; i < pathKeys.length - 1; i++) {
      if (currNode.get(pathKeys[i]) == null) {
        ((ObjectNode) currNode).set(pathKeys[i], createObjectNode());
      }
      currNode = currNode.get(pathKeys[i]);
    }
    if (value instanceof String) {
      return ((ObjectNode) currNode).put(pathKeys[pathKeys.length - 1], (String) value);
    } else if (value instanceof Double) {
      return ((ObjectNode) currNode).put(pathKeys[pathKeys.length - 1], (Double) value);
    } else if (value instanceof Long) {
      return ((ObjectNode) currNode).put(pathKeys[pathKeys.length - 1], (Long) value);
    } else if (value instanceof Integer) {
      return ((ObjectNode) currNode).put(pathKeys[pathKeys.length - 1], (Integer) value);
    } else if (value instanceof ArrayNode) {
      return (ObjectNode) ((ObjectNode) currNode).set(pathKeys[pathKeys.length - 1], (ArrayNode) value);
    } else if (value instanceof JsonNode) {
      return (ObjectNode) ((ObjectNode) currNode).set(pathKeys[pathKeys.length - 1], (JsonNode) value);
    } else if (value == null) {
      return ((ObjectNode) currNode).put(pathKeys[pathKeys.length - 1], (String) null);
    }
    return null;
  }

  public static BaseJsonNode parseString(String message) throws IOException {
    return (BaseJsonNode) mapper.readTree(message);
  }

  public static String escapeJsonStringValue(String value) {
    return org.apache.commons.lang3.StringEscapeUtils.escapeJson(value);
  }

  public static String writeAsString(Object node) throws JsonProcessingException {
    return mapper.writeValueAsString(node);
  }

  public static ObjectNode putNode(JsonNode origNode, String value, String... pathKeys) {
    return putNodeObject(origNode, value, pathKeys);
  }

  public static ObjectNode putNode(JsonNode origNode, JsonNode value, String... pathKeys) {
    return putNodeObject(origNode, value, pathKeys);
  }

  public static ObjectNode putNode(JsonNode origNode, ArrayNode value, String... pathKeys) {
    return putNodeObject(origNode, value, pathKeys);
  }

  public static ObjectNode putNode(JsonNode origNode, Double value, String... pathKeys) {
    return putNodeObject(origNode, value, pathKeys);
  }

  public static ObjectNode putNode(JsonNode origNode, Integer value, String... pathKeys) {
    return putNodeObject(origNode, value, pathKeys);
  }

  public static ObjectNode putNode(JsonNode origNode, Long value, String... pathKeys) {
    return putNodeObject(origNode, value, pathKeys);
  }

  public static ObjectNode putNode(JsonNode origNode, Object value, String... pathKeys) {
    return putNodeObject(origNode, value, pathKeys);
  }

  public static JsonNode getNode(JsonNode node, String query) throws JsonQueryException {
    return JsonQuery.compile(query).apply(node).get(0);
  }

  public static JsonNode getNodeAt(ObjectNode origNode, String... pathKeys) {
    if (origNode == null) {
      return null;
    }
    JsonNode currNode = origNode;
    for (int i = 0; i < pathKeys.length; i++) {
      if (currNode.get(pathKeys[i]) == null) {
        return null;
      }
      currNode = currNode.get(pathKeys[i]);
    }
    return currNode;
  }

  public static String toRawString(JsonNode node) {
    StringWriter sw = new StringWriter();
    try {
      new ObjectMapper().writer().writeValue(sw, node);
    } catch (Exception e) {
      return "Error serializing JSON node: " + e.getMessage();
    }
    return sw.toString();
  }

  public static Map<String, JsonNode> toMap(ObjectNode source) {
    MapType type = mapper.getTypeFactory()
        .constructMapType(HashMap.class, String.class, JsonNode.class);
    return mapper.convertValue(source, type);
  }

  public static Map<String, Object> toFullMap(ObjectNode source) {
    Map<String, Object> map = new HashMap<>();
    map = mapper.convertValue(source, map.getClass());
    return map;
  }

  public static String[] toArray(ArrayNode node) {
    String[] returnArray = new String[node.size()];
    for (int i = 0; i < node.size(); i++) {
      returnArray[i] = node.get(i).asText();
    }
    return returnArray;
  }

  // Search the array for all the entries of provided key and delete those elements from array
  // Returns the modified array
  public static ArrayNode removeArrayElementByKey(ArrayNode array, String targetKey) {
    if (array == null) {
      return null;
    }

    List<JsonNode> list = StreamSupport.stream(array.spliterator(), false).filter(element -> {
      JsonNode keyNode = element.get("key");
      if (keyNode != null) {
        String key = element.get("key").asText();
        return !key.equals(targetKey);
      } else {
        return false;
      }
    }).collect(Collectors.toList());

    return mapper.valueToTree(list);
  }

  // Merge arrays using right outer join (do not copy new buckets from source to target)
  public static ArrayNode rightOutterJoinArrays(ArrayNode source, ArrayNode target) {
    return mergeArrays(source, target, false);
  }

  // Merge arrays using full outer join (copy new buckets from source to target)
  public static ArrayNode fullOutterJoinArrays(ArrayNode source, ArrayNode target) {
    return mergeArrays(source, target, true);
  }

  private static ArrayNode mergeArrays(ArrayNode source, ArrayNode target, boolean fullOuterJoin) {
    if (source == null) {
      return target;
    }
    if (target == null) {
      return source;
    }
    toStream(source)
        .forEach(srcElement -> {
          String key = srcElement.get("key").asText();
          ObjectNode targetElement = findArrayElementByKey(target, key);
          // Throw value away if no matching target exists (left outer join)
          if (targetElement != null) {
            targetElement = mergeFields(srcElement, targetElement);
          } else {
            if (fullOuterJoin) {
              target.add(srcElement);
            }
          }
        });
    return target;
  }

  public static ObjectNode mergeFields(JsonNode source, ObjectNode target) {
    if (target == null) {
      target = createObjectNode();
    }
    for (String key : toList(source.fieldNames())) {
      final JsonNode sourceChildNode = source.get(key);
      final JsonNodeType nodeType = sourceChildNode.getNodeType();
      if (!key.equals("key") && !key.equals("buckets")) {
        switch (nodeType) {
          case STRING:
          case NUMBER:
          case NULL:
            target.set(key, sourceChildNode);
            break;
          case OBJECT:
            ObjectNode mergeSubtree = mergeFields(sourceChildNode, (ObjectNode) target.get(key));
            if (mergeSubtree.size() > 0) {
              target.set(key, mergeSubtree);
            }
            break;
          default:
//            throw new Exception("Unexpected source child node type during field copy (key: "
//                + key + ", type: " + nodeType.toString() + ")");
        }
      }
    }
    return target;
  }

  public static ObjectNode convertValue(Map object) {
    return mapper.convertValue(object, ObjectNode.class);
  }

  public static ArrayNode convertValue(List object) {
    return mapper.convertValue(object, ArrayNode.class);
  }

  public static Map<String, JsonNode> convertValue(ObjectNode object) {
    return mapper.convertValue(object, Map.class);
  }

  //iterate through ObjectNode and apply function to every (field, value) pair
  static public void iterateObjectNode(ObjectNode objectNode, BiConsumer<String, JsonNode> function){
    if (objectNode == null){
      return;
    }

    Iterator<Map.Entry<String, JsonNode>> it = objectNode.fields();
    while (it.hasNext()) {
      Map.Entry<String, JsonNode> entry = it.next();
      String key = entry.getKey();
      JsonNode value = entry.getValue();
      function.accept(key, value);
    }
  }

  // Scan array elements and return those having an attribute named 'key' with a
  // value matching targetKey, if any
  public static ObjectNode findArrayElementByKey(ArrayNode array, String targetKey) {
    if (array == null) {
      return null;
    }
    return findArrayElementByProvidedKey(array, "key", targetKey);
  }

  public static Set getArrayKeyList(ArrayNode array) {
    if (array == null) {
      return null;
    }

    Set keyList = new HashSet();
    for (JsonNode node: array) {
      keyList.add(node.get("key").asText());
    }

    return keyList;
  }

  public static ObjectNode findArrayElementByProvidedKey(ArrayNode array, String key, String targetValue) {
    if (array == null || key == null || targetValue == null) {
      return null;
    }

    ObjectNode matchingElement = (ObjectNode) toStream(array)
            .filter(element -> {
              JsonNode keyNode = element.get(key);
              if (keyNode != null) {
                String value = element.get(key).asText();
                return value.equals(targetValue);
              } else {
                return false;
              }
            })
            .findFirst()
            .orElse(null);
    return matchingElement;
  }

  /*
    get the raw value of JsonNode: Number, Text, Boolean or itself
    for example:
    In {"key": "tim hall", "doc_count": 91351}: TextNode = "tim hall" => String "tim hall"
    In {"key": "15.0", "doc_count": 91351}: DoubleNode = "15.0" => double 15.0
   */
  public static Object getNodeValue(JsonNode node){
    if (node instanceof NumericNode){
      return node.numberValue();
    } else if (node instanceof TextNode){
      return node.textValue();
    } else if (node instanceof BooleanNode){
      return node.booleanValue();
    } else if (node instanceof MissingNode || node instanceof NullNode){
      return node.asText();
    } else{
      // ObjectNode or ArrayNode, return itself
      return node;
    }
  }
}
