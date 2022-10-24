#!/bin/bash

rm -rf ./ios_target
mkdir "arm64"
mkdir "simulator"

mkdir "ios_target"
cd ios_target || exit
cmake .. -G Xcode -DCMAKE_TOOLCHAIN_FILE=../../ios.toolchain.cmake -DPLATFORM=OS64COMBINED
cmake --build . --config RelWithDebInfo

cp ./RelWithDebInfo-iphoneos/libaudio.a ../arm64/libaudio.a

#open ./audio.xcodeproj
xcodebuild -project audio.xcodeproj -scheme ALL_BUILD -destination "platform=iOS Simulator,name=iPhone 13" -configuration RelWithDebInfo

cp ./RelWithDebInfo-iphonesimulator/libaudio.a ../simulator/libaudio.a
cd ../
lipo -create ./arm64/libaudio.a ./simulator/libaudio.a -output ../pod/libaudio.a
