/**
 *  Copyright 2015 SmartThings V1.2.0
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
 *  This DTH was originally developed by SmartThings for a garage door. 
 *  Then modified by Robin Winbourne for use with a dog feeder to give access to four 'states'
 *  Then modified by me (@cobra) to change the text/colours/icons to be able to use it to show who took a shared car :)
 *
 */


metadata {
	definition (name: "Custom Car Indicator - Cars - Female", namespace: "Cobra", author: "SmartThings") {
		capability "Actuator"
		capability "Door Control"
    capability "Garage Door Control"
		capability "Refresh"
		capability "Sensor"
        
        command "car1"
        command "car2"
        command "at_home"
		command "car3"
       
	}

	simulator {
		
	}

	tiles {
		standardTile("toggle", "device.door", inactiveLabel: true, width: 3, height: 3) {
			state("closed", label:"Alfa", action:"door control.open", icon:"st.People.people2", backgroundColor:"#FF0416")
			state("open", label:"Mercedes", action:"door control.close", icon:"st.People.people2", backgroundColor:"#0808F9")
			state("opening", label:"At Home", icon:"st.People.people2", backgroundColor:"#04FF04")
			state("closing", label:"Rexton", icon:"st.People.people2", backgroundColor:"#FEC003")
			
		}
		standardTile("open", "device.door", inactiveLabel: false, decoration: "flat") {
			state "default", label:"At Home", action:"door control.open", icon:"st.People.people2"
		}
		
	
        standardTile("car2", "device.door", inactiveLabel: false, decoration: "flat") {
			state "default", label:"Mercedes", action:"car2", icon:"st.People.people2"
		}
        standardTile("car1", "device.door", inactiveLabel: false, decoration: "flat") {
			state "default", label:"Alfa", action:"car1", icon:"st.People.people2"
		}
	standardTile("close", "device.door", inactiveLabel: false, decoration: "flat") {
			state "default", label:"Rexton", action:"door control.close", icon:"st.People.people2"
		}


		main "toggle"
		details(["toggle", "open", "close", "car1", "car2"])
	}
}

def parse(String description) {
	log.trace "parse($description)"
}

def open() {
	sendEvent(name: "door", value: "opening")
}

def close() {
    sendEvent(name: "door", value: "closing")
}

def car1() {
    sendEvent(name: "door", value: "closed")
}

def car2() {
    sendEvent(name: "door", value: "open")
}

def at_home() {
    sendEvent(name: "door", value: "opening")
}

def car3() {
    sendEvent(name: "door", value: "closing")
}