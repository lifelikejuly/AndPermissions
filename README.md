# AndPermissions
[![](https://jitpack.io/v/lifelikejuly/AndPermissions.svg)](https://jitpack.io/#lifelikejuly/AndPermissions)
[![License](https://img.shields.io/badge/License%20-Apache%202-337ab7.svg)](https://www.apache.org/licenses/LICENSE-2.0)

how to request permissions 
```kotlin

     var checker =
     AndPermissions.Builder(context = context)
         .permissions(
             listOf(
                 Manifest.permission.WRITE_CALENDAR,
                 Manifest.permission.READ_CALENDAR,
                 Manifest.permission.CAMERA,
             )
         )
         .explainEachGroup(true) // true:explain each permission group how to use  false: only once explain all permission
         .onExplainPermission(object : OnExplainCallback {
             override fun onExplain(permissions: List<String>, onRun: OnPermissionRun) {
                 // why permissions need
                 // then  onRun.onRun() to request permission
                 // onRun.cancel() to cancel request permission
             }

         })
         .onPermissionCallback(object : OnResultCallback {
             override fun onPermissionResult(
                 isAllGranted: Boolean,
                 permissionResults: MutableMap<String, Boolean>
             ) {
                 if (isAllGranted) {
                    ///....
                 }
                 permissionResults.forEach {
                    /// ....
                 }
             }

         })
         .build()
 checker.request()
```
check beforehand activity is use permission 
```kotlin
/// 
@RequestPermissions(permissions = [
    Manifest.permission.WRITE_CALENDAR,
    Manifest.permission.READ_CALENDAR,
    Manifest.permission.ACCESS_FINE_LOCATION
])
class AcAnnotationPage : AppCompatActivity() {
    /// ...
}

AndPermissions.jumpActivityCheck(
 context,
 AcAnnotationPage::class.java,
 onResultCallback = object :
     OnResultCallback {
     override fun onPermissionResult(
         isAllGranted: Boolean,
         permissionResults: MutableMap<String, Boolean>
     ) {
         permissionResults.forEach {
            /// ....
         }

         if (isAllGranted) {
             // ....
         }
     }
 })
```
## implementation

```kotlin
implementation 'com.github.lifelikejuly.AndPermissions:AndPermissions:0.0.1'
implementation 'com.github.lifelikejuly.AndPermissions:annotation:0.0.1'
kapt 'com.github.lifelikejuly.AndPermissions:processor:0.0.1'
```

## TODO 
* special permissions
* explain permission 
* jump to permission setting

## License
```text
Copyright 2024 JulyYu

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```