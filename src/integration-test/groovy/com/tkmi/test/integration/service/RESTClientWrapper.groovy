package com.tkmi.test.integration.service

import groovyx.net.http.RESTClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.PropertySource
import org.springframework.stereotype.Service

@PropertySource('classpath:application-integration.properties')
@Service
class RESTClientWrapper {

  @Value('${integration.rest.host}') host
  @Value('${integration.rest.port}') port

  @Bean
  RESTClient restClient() {
    return new RESTClient("http://$host:$port")
  }
}
