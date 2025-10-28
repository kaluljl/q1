//
//  CitySimulatorApp.swift
//  CitySimulator
//
//  Created by AI进化论-花生 on 2024/01/01.
//

import SwiftUI

@main
struct CitySimulatorApp: App {
    let persistenceController = PersistenceController.shared
    
    var body: some Scene {
        WindowGroup {
            ContentView()
                .environment(\.managedObjectContext, persistenceController.container.viewContext)
        }
    }
}
