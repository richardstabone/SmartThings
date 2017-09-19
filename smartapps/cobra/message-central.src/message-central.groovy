/**
 *  ****************  Message Central  ****************
 *
 *  Design Usage:
 *  This is the 'Parent' app for message automation
 *
 *
 *  Copyright 2017 Andrew Parker
 *  
 *  This SmartApp is free!
 *  Donations to support development efforts are accepted via: 
 *
 *  Paypal at: https://www.paypal.me/smartcobra
 *  
 *
 *  I'm very happy for you to use this app without a donation, but if you find it useful then it would be nice to get a 'shout out' on the forum! -  @Cobra
 *  Have an idea to make this app better?  - Please let me know :)
 *
 *  Website: http://securendpoint.com/smartthings
 *
 *-------------------------------------------------------------------------------------------------------------------
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *-------------------------------------------------------------------------------------------------------------------
 *
 *  If modifying this project, please keep the above header intact and add your comments/credits below - Thank you! -  @Cobra
 *
 *-------------------------------------------------------------------------------------------------------------------
 *
 *  Last Update: 03/09/2017
 *
 *  Changes:
 *
 * 
 *
 *  V1.0.2 - added rename ability
 *  V1.0.1 - Header & Debug
 *  V1.0.0 - POC
 *
 */

 
 definition(
    name: "Message Central",
    namespace: "Cobra",
    author: "Andrew Parker",
    description: "Parent App for Message Automation.",
   category: "Fun & Social",
    iconUrl: "http://54.246.165.27/img/icons/cobra3.png",
    iconX2Url: "http://54.246.165.27/img/icons/cobra3.png",
    iconX3Url: "http://54.246.165.27/img/icons/cobra3.png")

preferences {
    
    page(name: "mainPage", title: "Automations", install: true, uninstall: true,submitOnChange: true) {
    
    section() {
        paragraph "V1.0.2"
        paragraph image: "http://54.246.165.27/img/icons/cobra3.png",
                  title: "Multiple Message Control",
                  required: false,
                  "This parent app is a container for all message child apps"
    }
    
        section {
            app(name: "switchMessageAutomation", appName: "Message_Central_Child", namespace: "Cobra", title: "Create New Triggered Message", multiple: true)
           
            }
            
            section("Parent name") {
                label title: "Enter a name for this Parent", required: false
           
            }
            
    }
}

def installed() {
    log.debug "Installed with settings: ${settings}"
    initialize()
}

def updated() {
    log.debug "Updated with settings: ${settings}"
    unsubscribe()
    initialize()
}

def initialize() {
    log.debug "there are ${childApps.size()} child smartapps"
    childApps.each {child ->
        log.debug "child app: ${child.label}"
    }
}
 
 
 
 
 
 
 
 
 