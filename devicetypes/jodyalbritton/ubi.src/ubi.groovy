/**
 *  Ubi
 *
 *  Copyright 2014 Jody Albritton
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
metadata {
	definition (name: "Ubi", namespace: "jodyalbritton", author: "jody albritton") {
		capability "Refresh"
   		capability "Polling"
        capability "Temperature Measurement"
        capability "Relative Humidity Measurement"
        capability "Sensor"
        capability "Illuminance Measurement"
        capability "Speech Synthesis"
        
        attribute "soundlevel", "enum"
		attribute "airpressure", "enum"
	}

	simulator {
		// TODO: define status and reply messages here
	}
    
    preferences {
    	 input "tempPref", "bool", title: "Farenheit",
              description: "Temp meausurements in Farenheit.", defaultValue: true,
              required: false, displayDuringSetup: true
    }
	
  

  
	tiles(scale: 2) {
        multiAttributeTile(name:"richTemperature", type:"generic", width:6, height:4) {
        	tileAttribute("device.temperature", key: "PRIMARY_CONTROL") {
      			attributeState "temperature", label: '${currentValue}°', backgroundColor:"#ffa81e"
        	}
        }

        valueTile("humidity", "device.humidity", inactiveLabel: false) {
            state "default", label:'${currentValue}%', unit:"Humidity"
        }
        valueTile("illuminance", "device.illuminance", inactiveLabel: false) {
			state "luminosity", label:'${currentValue}lux', unit:"Light"
		}
        valueTile("airpressure", "device.airpressure", inactiveLabel: false) {
        	state "default", label:'${currentValue}Kpa', unit:"Air Pressure"
        }
        valueTile("soundlevel", "device.soundlevel", inactiveLabel: false) {
        	state "default", label:'${currentValue}db', unit:"Sound"
        }
        standardTile("refresh", "device.poll", inactiveLabel: false, decoration: "flat") {
            state "default", action:"polling.poll", icon:"st.secondary.refresh"
        }
        
        main(["richTemperature", "humidity","illuminance", "soundlevel", "airpressure"])
        details(["richTemperature", "humidity", "illuminance", "soundlevel", "airpressure", "refresh"])
        
	}
}

// parse events into attributes
    def parse(String description) {
	log.debug "Parsing '${description}'"
	// TODO: handle 'humidity' attribute
	// TODO: handle 'illuminance' attribute
	// TODO: handle 'temperature' attribute

}



// handle commands

// send speech command 


void speak(message) {
    log.debug "Executing 'speak' using parent SmartApp for ${device.deviceNetworkId}"

    parent.speakChild(device.deviceNetworkId, message)
}


//poll the device
private def scale(){
  return "F"
}


void poll() {
	log.debug  settings.tempPref
	log.debug "Executing 'poll' using parent SmartApp for ${device.deviceNetworkId}"
    def results =  parent.pollChild(device.deviceNetworkId)
    
    parseEvents(results)	
}


//Parse the events 
def parseEvents(results) {
		def temp = results.temperature.toBigDecimal()
        def curTemp = cToF(temp)
		
	 	sendEvent(name: 'temperature', value: curTemp as Integer)
        sendEvent(name: 'humidity', value: results.humidity)
        sendEvent(name: 'illuminance', value: results.light)
        sendEvent(name: 'soundlevel', value: results.soundlevel)
        sendEvent(name: 'airpressure', value: results.airpressure)
        
        log.debug "Results: ${results}"
        
        
}


  

def cToF(temp) {
	if (settings.tempPref){ 
		return temp * 1.8 + 32
     
    }else{ 
    	return temp
     
    }
}


def refresh() {
   
	log.debug "Executing 'refresh'"
   
	poll()
   	

    
}