package com.tkmi.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tkmi.utils.DemoUtility;
import com.tkmi.serialization.RequestProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class DemoService {

  @Autowired DemoUtility demoUtility;

  public ResponseEntity<ObjectNode> agg(RequestProperties properties) {
    Object object = demoUtility.getValueByKey(properties.getMessage(), properties.getKey());
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode result = (ObjectNode) mapper.createObjectNode().set("data", mapper.valueToTree(object));
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

}