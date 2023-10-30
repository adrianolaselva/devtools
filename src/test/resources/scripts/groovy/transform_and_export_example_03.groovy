package scripts.groovy

import groovy.json.JsonOutput
import groovy.json.JsonSlurper

static String apply(String payload) {
    def json = new JsonSlurper()
    def payloadObject = json.parseText(payload)
    payloadObject.remove('last_name')
    return JsonOutput.toJson(payloadObject)
}