# Android Utils
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.imanfz/android-utils/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.imanfz/android-utils)

[![](https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/iman-faizal) [![](https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white)](https://github.com/imanfz)

![](https://img.shields.io/badge/Freelancer-29B2FE?style=for-the-badge&logo=Freelancer&logoColor=white) ![](https://img.shields.io/badge/Apple%20laptop-333333?style=for-the-badge&logo=apple&logoColor=white)

## Preview
![enter image description here](https://github.com/imanfz/android-utils/blob/master/images/preview.gif)

## Feature
- Adapter
  - [TabPagerAdapter]
- Base
  - [BaseActivity](#baseactivity)
  - [BaseBottomSheetDialog](#basebottomsheetdialog)
  - [BaseDialogFragment](#basedialogfragment)
  - [BaseFragment](#basefragment)
- Common
  - [SafeClickListener]
  - [SingleLiveData](#singlelivedata)
- Dialog
  - [LoadingDialog](#loadingdialog)
  - [ImageViewerDialog](#imageviewerdialog)
- Extension
  - [ActivityExt](#activityext)
  - [BitmapExt](#bitmapext)
  - [ContextExt](#contextext)
  - [DateExt]
  - [EditTextExt](#edittextext)
  - [FragmentExt](#fragmentext)
  - [IntegerExt](#integerext)
  - [ImageExt](#imageext)
  - [LogExt](#logext)
  - [NavigationExt](#navigationext)
  - [RecyclerViewExt](#recyclerviewext)
  - [StringExt](#stringext)
  - [TextViewExt](#textviewext)
  - [ViewBindingExt](#viewbindingext)
  - [ViewExt](#viewext)
  - [UriExt]
- Custom UI
  - [ReadMoreTextView](#readmoretextview)
  - [LoadingButton](#loadingbutton)
- Utils
  - [AppUpdateUtils](#appupdateutils)
  - [DateUtils](#dateutils)
  - [DeviceUtils](#deviceutils)
  - [DiffUtils](#diffutils)
  - [MetricsUtils](#metricsutils)
  - [NetworkUtils](#networkutils)
  - [RootUtils](#rootutils)
  - [ShimmerUtils](#shimmerutils)
  - [ValidationFrom](#validationform)
  - [ViewPagerUtils](#viewpagerutils)
- Other
  - [RecyclerViewEndlessScrollListener]
  - [AppPrefs](#appprefs)

## How to use

### Maven central
```
  dependencies {
      implementation 'io.github.imanfz:android-utils:{latest version}'
  }
```


## Base
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
or if you have custom BaseFragment, you can extends it:
```
class BaseFragment<B : ViewBinding> : BaseFragment<B>() {
    ...
}
```

## Common
### [SingleLiveData](#singlelivedata)
for handle bug live data in fragment after on backstack
```
  val dataState = SingleLiveData<String>()
```

## Dialog
### [ImageViewerDialog](#imageviewerdialog)
```
  val image = url or uri.toString()
  ImageViewerDialog.newInstance(image).show(supportFragmentManager, tag)
```
### [LoadingDialog](#loadingdialog)
```
class MainActivity : BaseActivity<ActivityMainBinding>() {
    ...
    binding.apply {
        btnLoading.setSafeOnClickListener {
            showLoading()
            lifecycle.coroutineScope.apply {
               launch {
                  delay(3000)
                  hideLoading()
               }
            }
        }
    }
}

## Extension
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

update language
```
open class BaseActivity : AppCompatActivity() {
    override fun attachBaseContext(newBase: Context) {
        newBase.apply {
            val prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            var lang = prefs.getString(AppPreference.KEY_LANGUAGE, "en") ?: "en"
            super.attachBaseContext(updateBaseContextLocale(lang))
        }
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
### [NavigationExt](#navigationext)
```
findNavController().safeNavigate(action)
findNavController().safeNavigate(R.id.navigateto)
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
            logi(getStartAndEndOfSubstring("Fai")) // output (5, 7) 
        }
        "afas37h38f".getNumeber() // output: 3738
         
    }
}
```
### [TextViewExt](#textviewext)
```
fromHtmlText(text: String)
setColorOfSubstring(substring: String, color: Int)
startDrawable(drawableRes: Int)
startDrawable(drawable: Drawable?) 
endDrawable(drawableRes: Int)
endDrawable(drawable: Drawable?) 
AutoCompleteTextView.setupDropdownUI()
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
### [AppUpdateUtils](#appupdateutils)
```
class MainActivity : BaseActivity<ActivityMainBinding>() {
    private lateinit var appUpdateUtils: AppUpdateUtils
    ...
    override fun onCreate(savedInstanceState: Bundle?) {
        appUpdateUtils = AppUpdateUtils(this)
    }
    
    override fun onResume() {
        super.onResume()
        appUpdateUtils.onResume()
    }
    
    override fun onDestroy() {
        appUpdateUtils.onDestroy()
        super.onDestroy()
    }
    
    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        appUpdateUtils.onActivityResult(requestCode, resultCode)
        super.onActivityResult(requestCode, resultCode, data)
    }
}
```

## Custom UI
### [ReadMoreTextView](#readmoretextview)
in layout
```
     <com.imanfz.utility.ui.ReadMoreTextView
        android:id="@+id/readMoreTextView"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:readMoreText="Show" // default Read More
        app:readMoreTextColor="@color/colorPrimary" // default readmore_color
        app:readMoreMaxLine="2" // default 4
        android:text="testsfsf sfks\nsfsfsf\nsfjfsjsfj\njsfjs oke bos"
```
### [LoadingButton](#loadingbutton)
in layout
```
 <com.imanfz.utility.ui.LoadingButton
        android:id="@+id/loading_button"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Loading Button"
        android:textColor="#ffffff"
        app:lb_buttonColor="@color/bluePrimaryDark"
        app:lb_loaderColor="@color/redPrimary"
        app:lb_loaderWidth="2dp"
        app:lb_loaderMargin="8dp"
        app:lb_isLoading="false"
        app:lb_cornerRadius="8dp"
        app:lb_isCircular="false"
        app:lb_isShadowEnable="false"
        app:lb_shadowColor="@color/yellowPrimary"
        app:lb_shadowHeight="2dp"
        app:lb_isStrokeEnable="true"
        app:lb_strokeWidth="2dp"
        app:lb_strokeColor="@color/redPrimary" />
```

in activity/fragment
```
loadingButton.apply {
    setSafeOnClickListener {
        showLoading()
        delayOnLifecycle {
            hideLoading()
        }
    }
}
```

## Utility
### [DateUtils](#dateutils)
```
getCurrentDateTime() // date
getCurrentDateTimeMils() // long
showDatePicker(context: Context) { date -> // string return
  ...
}
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
setLightTheme()
setDarkTheme()
```
### [DiffUtils](#diffutils)
mandatory, your model class extend the BaseModel. The detail base model is:
```
interface BaseModel {
    fun contains(someValue: String): Boolean = false
    fun isItemSameWith(value: BaseModel): Boolean = false
    fun isContentSameWith(value: BaseModel): Boolean = false
}
```
and how to use it
```
@Parcelize
data class Example(
  ...
  ...
): Parcelable, BaseModel
```
```
ItemDiffCallback<Example>()
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

NetworkStatusUtils(context).observe(this, {
    when(it) {
        NetworkStatus.Available -> shortToast("Network Connection Established")
        NetworkStatus.Unavailable -> shortToast("No Internet")
        NetworkStatus.Lost -> shortToast("Reconnecting")
    }
})
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

// extension
show()
hide()
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

## Other
- [AppPrefs](#appprefs)
```
putIsLogin(value: Boolean)
isLogin(): Boolean
putFirstTimeLaunch(value: Boolean)
isFirstTimeLaunch(): Boolean
isFirstLogin(): Boolean
putIsFirstLogin(value: Boolean)
putString(key: String, text: String)
getString(key: String): String
putInt(key: String, value: Int)
getInt(key: String): Int
clear()
// model
put(UserModel(), USER_KEY)
get<UserModel>(USER_KEY) // nullable

// list model
putList(listUserModel, LIST_KEY)
geListt<UserModel>(LIST_KEY)

putInitialNameAvatar(bitmap: Bitmap)
getInitialNameAvatar(): Bitmap?
```

![](https://img.shields.io/badge/Android_Studio-3DDC84?style=for-the-badge&logo=android-studio&logoColor=white) ![](https://img.shields.io/badge/Kotlin-0095D5?&style=for-the-badge&logo=kotlin&logoColor=white)
