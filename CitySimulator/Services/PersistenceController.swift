//
//  PersistenceController.swift
//  CitySimulator
//
//  Created by AI进化论-花生 on 2024/01/01.
//

import CoreData

struct PersistenceController {
    static let shared = PersistenceController()
    
    static var preview: PersistenceController = {
        let result = PersistenceController(inMemory: true)
        let viewContext = result.container.viewContext
        
        // 创建预览数据
        let city = CityEntity(context: viewContext)
        city.name = "预览城市"
        city.level = 1
        city.experience = 0
        
        do {
            try viewContext.save()
        } catch {
            let nsError = error as NSError
            fatalError("预览数据创建失败: \(nsError), \(nsError.userInfo)")
        }
        return result
    }()
    
    let container: NSPersistentContainer
    
    init(inMemory: Bool = false) {
        container = NSPersistentContainer(name: "CitySimulator")
        if inMemory {
            container.persistentStoreDescriptions.first!.url = URL(fileURLWithPath: "/dev/null")
        }
        container.loadPersistentStores(completionHandler: { (storeDescription, error) in
            if let error = error as NSError? {
                fatalError("Core Data加载失败: \(error), \(error.userInfo)")
            }
        })
        container.viewContext.automaticallyMergesChangesFromParent = true
    }
}
