# Customised Version of Cordova ZXing Plugin

References:
https://github.com/zxing/zxing

https://github.com/journeyapps/zxing-android-embedded

https://github.com/marceloburegio/cordova-plugin-zxing


This library enables an inverted scan, enabling scan on dark background with white patterns.

Works only on Android devices.

Updated ZXing:Core to version 3.4.1


## Barcode formats supported

| 1D product            | 1D industrial | 2D             |
|:----------------------|:--------------|:---------------|
| UPC-A                 | Code 39       | QR Code        |
| UPC-E                 | Code 93       | Data Matrix    |
| EAN-8                 | Code 128      | Aztec          |
| EAN-13                | Codabar       | PDF 417        |
| UPC/EAN Extension 2/5 | ITF           | MaxiCode       |
|                       |               | RSS-14         |
|                       |               | RSS-Expanded   |

## Installation

    cordova plugin add cordova-plugin-qrscan

### Check and request for Camera Permission

    cordova.plugins.scanner.permission(onSuccess, onFailure)

### Scan barcode

    cordova.plugins.scanner.scan(params, onSuccess, onFailure)

Arguments:

- **params**: All parameters are optional:

    ```javascript
    {
        'prompt_message':'Scan a barcode', // Change the info message. A blank message ('') will show a default message
        'orientation_locked':true, // Lock the orientation screen
        'camera_id':0, // Choose the camera source
        'beep_enabled':true, // Enables a beep after the scan
        'scan_type':'normal', // Types of scan mode: normal = default black with white background / inverted = white bars on dark background / mixed = normal and inverted modes
        'barcode_formats':[
            'QR_CODE',
            'CODE_39',
            'CODE_128'], // Put a list of formats that the scanner will find. A blank list ([]) will enable scan of all barcode types
        'extras':{} // Additional extra parameters. See [ZXing Journey Apps][1] IntentIntegrator and Intents for more details
    }
    ```

- **onSuccess**: function (s) {...} _Callback for successful scan._
- **onFailure**: function (s) {...} _Callback for cancelled scan or error._

Return:

- success('Permission is granted') _Acquire permission success_
- success('Permission already granted') _Permission already acquired_
- error('Permission not granted') _If user denied permission_

- success('scanned bar code') _Successful scan with value of scanned code_
- error('Scan Cancelled') _If user cancelled the scan (with back button etc)_

- error('misc error message') _Misc failure_


[1]: https://github.com/ricku44/cordova-plugin-scanner.git
