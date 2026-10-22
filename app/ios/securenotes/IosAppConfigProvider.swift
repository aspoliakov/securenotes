//
//  IosAppConfigProvider.swift
//  Securenotes
//

import Foundation
import app

class IosAppConfigProvider: AppConfigProvider {

    private let versionName: String
    private let versionCode: Int32

    override init() {
        versionName = Bundle.main.object(forInfoDictionaryKey: "CFBundleShortVersionString") as! String
        let parts = versionName.split(separator: ".").map { Int32($0)! }
        let companion = AppConfigProvider.companion
        versionCode = parts[0] * companion.MAJOR_VERSION_MULTIPLIER + parts[1] * companion.MINOR_VERSION_MULTIPLIER + parts[2]
        super.init()
    }

    override func getEnvironment() -> Environment {
        return Environment.master
    }

    override func getVersionCode() -> Int32 {
        return versionCode
    }

    override func getVersionName() -> String {
        return versionName
    }

    override func getBuildType() -> BuildType {
        #if DEBUG
        return BuildType.debug
        #else
        return BuildType.release_
        #endif
    }
}
