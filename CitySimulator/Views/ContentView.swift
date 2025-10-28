//
//  ContentView.swift
//  CitySimulator
//
//  Created by AI进化论-花生 on 2024/01/01.
//

import SwiftUI

struct ContentView: View {
    @StateObject private var gameManager = GameManager()
    
    var body: some View {
        NavigationView {
            MainGameView()
                .environmentObject(gameManager)
        }
        .navigationViewStyle(StackNavigationViewStyle())
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
