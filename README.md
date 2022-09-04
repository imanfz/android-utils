# Android Utils

[![](https://jitpack.io/v/imanfz/android-utils.svg)](https://jitpack.io/#imanfz/android-utils)

[![](https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/iman-faizal) [![](https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white)](https://github.com/imanfz)

![](https://img.shields.io/badge/Freelancer-29B2FE?style=for-the-badge&logo=Freelancer&logoColor=white) ![](https://img.shields.io/badge/Apple%20laptop-333333?style=for-the-badge&logo=apple&logoColor=white)
## Feature
- Base
  - [BaseActivity](#baseactivity)
  - [BaseBottomSheetDialog](#basebottomsheetdialog)
  - [BaseDialogFragment](#basedialogfragment)
  - [BaseFragment](#basefragment)
- Common
  - SafeClickListener
- Extension
  - [ActivityExt](#activityext)
  - [ContextExt](#contextext)
  - DateExt
  - [EditTextExt](#edittextext)
  - [FragmentExt](#fragmentext)
  - [IntegerExt](#integerext)
  - [ImageExt](#imageext)
  - [LogExt](#logext)
  - [RecyclerViewExt](#recyclerviewext)
  - [StringExt](#stringext)
  - [ViewBindingExt](#viewbindingext)
  - [ViewExt](#viewext)
- Utils
  - [DateUtils](#dateutils)
  - [DeviceUtils](#deviceutils)
  - [MetricsUtils](#metricsutils)
  - [NetworkUtils](#networkutils)
  - [RootUtils](#rootutils)
  - [ShimmerUtils](#shimmerutils)
  - [ValidationFrom](#validationform)
  - [ViewPagerUtils](#viewpagerutils)

## How to use

Add it to your build.gradle with:
```gradle
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```
and:

```gradle
dependencies {
    implementation 'com.github.imanfz:utils:{latest version}'
}
```
### [BaseActivity](#baseActivity)
```
class MainActivity : BaseActivity<ActivityMainBinding>() {
    ...
}
```
or if you have custom BaseActivity, you can extends it:
```
class BaseActivity<B : ViewBinding> : BaseActivity<B>() {
    ...
}
```
### [BaseBottomSheetDialog](#basebottomsheetdialog)
```
class SampleSheet : BaseBottomSheetDialog<FragmentSampleSheetBinding>() {
    ...
}
```
### [BaseFragment](#basefragment)
```
class SampleFragment : BaseFragment<FragmentSampleBinding>() {
    ...
}
```
or if you have custom BaseActivity, you can extends it:
```
class BaseFragment<B : ViewBinding> : BaseFragment<B>() {
    ...
}
```
### [ActivityExt](#activityext)
```
class MainActivity : BaseActivity<ActivityMainBinding>() {
    override fun onCreate(savedInstanceState: Bundle) {
        super.onCreate()
        ...
        
        // keyboard
        hidekeyboard()
        showKeyboard()
        
        // SnackBar
        longSnack("Hello World")
        shortSnack("Hello World")
    }
}
```
### [ContextExt](#contextext)
```
class MainActivity : BaseActivity<ActivityMainBinding>() {

    private val listPermission = listOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    
    override fun onCreate(savedInstanceState: Bundle) {
        super.onCreate()
        ...
       
        // Toast
        longToast("Hello World")
        shortToast("Hello World")
        
        if (hasPermission(Manifest.permission.CAMERA)) {
            logi("GRANTED")
        }
        
        if(hasPermission(listPermission.toTypedArray())) {
            logi("GRANTED")
        } else {
            requestPermission()
        }
        
        startActivity<HomeActivity>()
        // or
        startActivity<HomeActivity>(Bundle().apply{
            putString("A", "tes")
        })
        
        startActivity<HomeActivity>()
        // or
        startActivityAndClearTask<HomeActivity>(Bundle().apply{
            putString("A", "tes")
        })
        
        startActivityClearTop<HomeActivity>()
        // or
        startActivityClearTop<HomeActivity>(Bundle().apply{
            putString("A", "tes")
        })
        
        showBasicDialog(
            "Title",
            "Message",
            okClicked = { // nullable
                logi("OK")
            },
            cancelClicked = { // nullable
                logi("Cancel")
            }
        )
    }
}
```
### [EditTextExt](#edittextext)
```
class MainActivity : BaseActivity<ActivityMainBinding>() {
    override fun onCreate(savedInstanceState: Bundle) {
    super.onCreate()
    ...
        val number = binding.etNumber.getString()
    }
}
```
### [FragmentExt](#fragmentext)
```
class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    private val listPermission = listOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    
    override fun onViewCreated(savedInstanceState: Bundle) {
        super.onViewCreated()
        ...
       
        // SnackBar
        longSnack("Hello World")
        shortSnack("Hello World")
        
        // Toast
        longToast("Hello World")
        shortToast("Hello World")
        
        // for finished parent activity
        finish()
        
        copyTextToClipboard(binding.etNumber.getString(), "Text Copied")
        
        if (hasPermission(Manifest.permission.CAMERA)) {
            logi("GRANTED")
        }
        
        if(hasPermission(listPermission.toTypedArray())) {
            logi("GRANTED")
        } else {
            requestPermission()
        }
        
        startActivity<HomeActivity>()
        // or
        startActivity<HomeActivity>(Bundle().apply{
            putString("A", "tes")
        })
        
        startActivity<HomeActivity>()
        // or
        startActivityAndClearTask<HomeActivity>(Bundle().apply{
            putString("A", "tes")
        })
        
        startActivityClearTop<HomeActivity>()
        // or
        startActivityClearTop<HomeActivity>(Bundle().apply{
            putString("A", "tes")
        })
        
        showDialog(
            "Title",
            "Message",
            okClicked = { // nullable
                logi("OK")
            },
            cancelClicked = { // nullable
                logi("Cancel")
            }
        )
    }
}
```
### [IntegerExt](#integerext)
```
class MainActivity : BaseActivity<ActivityMainBinding>() {
    override fun onCreate(savedInstanceState: Bundle) {
    super.onCreate()
    ...
        val needDp = 8.toDp()
        val needPx = 8.toPx()
    }
}
```
### [ImageExt](#imageext)
```
class MainActivity : BaseActivity<ActivityMainBinding>() {
    override fun onCreate(savedInstanceState: Bundle) {
    super.onCreate()
    ...
        // basic
        binding.ivAvatar.loadImage("url")
        // or
        val uri = ...
        binding.ivAvatar.loadImage(uri)
        // or
        binding.ivAvatar.loadImage(R.drawable.ic_avatar)
        
        // circle
        binding.ivAvatar.loadCircleImage("url")
        // or
        val uri = ...
        binding.ivAvatar.loadCircleImage(uri)
        // or
        binding.ivAvatar.loadCircleImage(R.drawable.ic_avatar)
        
        // rounded (default radius = 10, can modify)
        binding.ivAvatar.loadRoundedImage("url", 20)
        // or
        val uri = ...
        binding.ivAvatar.loadRoundedImage(uri, 15)
        // or
        binding.ivAvatar.loadRoundedImage(R.drawable.ic_avatar, 25)
        
        // ShapeableImageView
        binding.shapableIv.setRadius(10)
    }
}
```
### [LogExt](#logext)
```
class MainActivity : BaseActivity<ActivityMainBinding>() {
    override fun onCreate(savedInstanceState: Bundle) {
    super.onCreate()
    ...
    
        // TAG auto generate with class T.TAG
        // example use get HomeFragment TAG
        HomeFragment().TAG
        
        logv("Hello World")
        logi("Hello World")
        logw("Hello World")
        logd("Hello World")
        loge("Hello World")
    }
}
```
### [RecyclerViewExt](#recyclerviewext)
```
class MainActivity : BaseActivity<ActivityMainBinding>() {
    override fun onCreate(savedInstanceState: Bundle) {
    super.onCreate()
    ...
    
        binding.rvMain.apply {
            setup() // default is LinearLayourManager
            // or
            setup(GridLayoutManager(this, 4))
            
            addVerticalItemDecoration() // default
            // or
            addVerticalItemDecoration(ContexCompact.getDrawable(this, R.drawable.rect_blue)) // with drawable nullable
            
            addDividerVerticalItemExLast(ContexCompact.getDrawable(this, R.drawable.rect_blue)) // with drawable nullable
            
            addDividerVerticalItemExHeader(ContexCompact.getDrawable(this, R.drawable.rect_blue)) // with drawable nullable
            
            addHorizontalItemDecoration()
            
            addHorizontalSpaceItem(space = 8)
            
            addGridSpaceItem(
                spaceCount: 4,
                rowSpace: 8f,
                columnSpacing: 24f
            )              
        }
    }
}
```

### [StringExt](#stringext)
```
class MainActivity : BaseActivity<ActivityMainBinding>() {
    override fun onCreate(savedInstanceState: Bundle) {
    super.onCreate()
    ...
    
        "11/11/2022".toDate("dd/MM/yyyy")
        "Iman Faizal".apply {
            logi(getFirstName()) // output: Iman
            logi(getLastName()) // output: Faizal
            // get initial can set limitation for your need initial name, example getInitial(limit = 1) -> result I
            logi(getInitials()) // output: IF
        }
        "afas37h38f".getNumeber() // output: 3738
         
    }
}
```
### [ViewBindingExt](#viewbindingext)
   - ViewHolder
        ```
        inner class CharactersViewHolder(binding: RowCharacterBinding): BindingViewHolder<RowCharacterBinding>(binding) {    
            fun bind(item: Character) {
                ....
                binding.tvName.text = item.name
                ....
                binding.root.setOnClickListener {
                   ....
                }
            }    
        }
        ```
   - Adapter
        ```
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return CharactersViewHolder(parent.toBinding())
        }
        ```
   - [BaseActivity](#baseactivity)
   - [BaseFragment](#basefragment) 
   - [BaseBottomSheetDialog](#basebottomsheetdialog)
   - [BaseDialogFragment](#basedialogfragment)

### [ViewExt](#viewext)
```
class MainActivity : BaseActivity<ActivityMainBinding>() {
    override fun onCreate(savedInstanceState: Bundle) {
    super.onCreate()
    ...
        binding.apply {
            imageView.gone() // hide
            imageView.isGone() // true or false
            imageView.visible() // visibile
            imageView.isVisible() // true or false
            imageView.invisible() // invisibile
            imageView.isInvisible() // true or false
            imageView.enabled()
            imageView.disabled()
            
            root.snackBarWithAction(
                "Message",
                "Action Label",
                onClicked = {
                    logi("OK click")
                }
            )
        }
    }
}
```
### [DateUtils](#dateutils)
```
getCurrentDateTime() // date
getCurrentDateTimeMils() // long
showDatePicker(context: Context, textView: TextView) // textview for view on clicked
```
### [DeviceUtils](#deviceutils)
```
getUUID() // UUID
getDeviceModel()
getDeviceName()
getDeviceAPILevel()
getDeviceOSCode()
getDeviceOSName()
getDeviceTimeZone()
getDeviceLanguage()
```
### [MetricsUtils](#metricsutils)
```
convertDpToPixel(dp: Float, context: Context?): Float
convertPixelsToDp(px: Float, context: Context?): Float
```
### [NetworkUtils](#networkutils)
```
isConnectionOn(context: Context): Boolean
isInternetAvailable(): Boolean
```
### [RootUtils](#rootutils)
```
isDeviceRooted(): Boolean
```
### [ShimmerUtils](#shimmerutils)
```
reverse(duration: Long = 2000L): Shimmer
thinStraightTransparent(): Shimmer
sweep(
    direction: Int = Shimmer.Direction.TOP_TO_BOTTOM,
    tilt: Float = 0f
): Shimmer
spotlight(
    alpha: Float = 0f,
    duration: Long = 2000L,
    dropOff: Float = 0.1f,
    intensity: Float = 0.35f,
    shape: Int = Shimmer.Shape.RADIAL
): Shimmer
```
### [ValidaionForm](#validaitonform)
```
isValidInput(text: String, textInputLayout: TextInputLayout): Boolean
isValidInputLength(text: String, textInputLayout: TextInputLayout): Boolean
isValidEmail(emailText: String, textInputLayout: TextInputLayout): Boolean
isValidPhone(phone: String, textInputLayout: TextInputLayout): Boolean
isValidPhoneOrEmail(text: String, textInputLayout: TextInputLayout): Boolean
isValidPassword(password: String, textInputLayout: TextInputLayout): Boolean
isValidStrongPassword(password: String, textInputLayout: TextInputLayout): Boolean

enum class PasswordValidation {
    LOWERCASE, UPPERCASE, DIGIT, CHARACTER
}
isNewValidPassword(password: String): List<PasswordValidation>
isMatchPassword(password: String, confirmPassword: String, textInputLayout: TextInputLayout): Boolean
```
### [ViewPagerUtils](#viewpagerutils)
```
DepthPageTransformer()
ZoomOutPageTransformer()
```

![](https://img.shields.io/badge/Android_Studio-3DDC84?style=for-the-badge&logo=android-studio&logoColor=white) ![](https://img.shields.io/badge/Kotlin-0095D5?&style=for-the-badge&logo=kotlin&logoColor=white)
