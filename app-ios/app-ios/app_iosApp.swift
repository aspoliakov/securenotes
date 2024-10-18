//
//  app_iosApp.swift
//  app-ios
//
//  Created by Anton Poliakov on 01.12.2023.
//

import SwiftUI
import Firebase

@main
struct iOSApp: App {
    
    init(){
        FirebaseApp.configure()
    }
    
    var body: some Scene {
        WindowGroup {
			ContentView()
		}
	}
}
