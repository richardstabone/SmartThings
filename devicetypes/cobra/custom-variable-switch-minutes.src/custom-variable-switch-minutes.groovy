/**
 *  Copyright 2015 SmartThings
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
 *
 *  Modified to show different colours/text and auto switch off
 *
 *
 */
metadata {

    definition (name: "Custom Variable Switch - Minutes", namespace: "Cobra", author: "smartthings") {
		capability "Switch"
        capability "Relay Switch"
		capability "Sensor"
		capability "Actuator"

		command "onPhysical"
		command "offPhysical"
	}


preferences {
		
		
	
	
		section {
			input "delayNum", type: "number", title: "Minutes delay before 'Off'", description: ""
		}


		

 }


	tiles {
		standardTile("switch", "device.switch", width: 2, height: 2, canChangeIcon: true) {
			state "off", label: 'OffMin',  icon: "st.switches.switch.off", backgroundColor: "#0808F9"
			state "on", label: 'OnMin',  icon: "st.switches.switch.on", backgroundColor: "#FF0416"
		}
		standardTile("on", "device.switch", decoration: "flat") {
			state "default", label: 'OnMin', action: "onPhysical", backgroundColor: "#ffffff"
		}
		standardTile("off", "device.switch", decoration: "flat") {
			state "default", label: 'OffMin', action: "offPhysical", backgroundColor: "#ffffff"
		}
        main "switch"
		details(["switch","on","off"])
	}
}

def parse(description) {
}

def on() {
def delay1 = delayNum as int
def delay2 = delay1 * 60
	log.debug "$version on()"
	sendEvent(name: "switch", value: "on")
    runIn(delay2, off,[overwrite: false])
}

def off() {
	log.debug "$version off()"
	sendEvent(name: "switch", value: "off")
}

def onPhysical() {
def delay1 = delayNum as int
def delay2 = delay1 * 60
	log.debug "$version onPhysical()"
	sendEvent(name: "switch", value: "on", type: "physical")
    runIn(delay2, offPhysical,[overwrite: false])
}

def offPhysical() {
	log.debug "$version offPhysical()"
	sendEvent(name: "switch", value: "off", type: "physical")
}

private getVersion() {
	"PUBLISHED"
}

def myDelay() {


}