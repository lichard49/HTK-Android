# HTK-Android
Port of [Hidden Markov Toolkit (HTK)](http://htk.eng.cam.ac.uk/), a C library for working with hidden markov models for speech recognition, to Android OS using the Native Development kit.  Adapts techniques from the [Georgia Tech Gesture Toolkit (GT2K)](http://gt2k.cc.gatech.edu/), which is based on HTK.

The project was built using Android Studio 2.2, which has new support for CMake.

Currently, only the HVite tool is ported.  This tool is the HTK implementation of Viterbi's algorithm, which accepts trained models and a test frame and returns likelihoods for each of the models.  *TODO:* On-device training, especially for user adaptive models.

## Usage
Use the `createExtFile` method to convert a test frame into binary format useable by HTK.  Then, pass the .ext file to the native function.  The result is stored in the `recognition_result` file.  *TODO:* Properly implement function parameters/return value instead of using the file system.
