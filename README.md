# DaggerAutoInject
**Inject automatically your Activities & Fragments & ViewModels, just with a simple annotation**

## Download

```groovy
def dagger_version = "2.14.1"

dependencies {
    // aac
    implementation "android.arch.lifecycle:extensions:1.1.0"
    annotationProcessor "android.arch.lifecycle:compiler:1.1.0"
    
    // dagger auto inject
    implementation 'com.github.florent37:dagger-auto-inject:1.0.0'
    annotationProcessor 'com.github.florent37:dagger-auto-inject-compiler:1.0.0'

    //dagger2
    compile "com.google.dagger:dagger:$dagger_version"
    compile "com.google.dagger:dagger-android:$dagger_version"
    compile "com.google.dagger:dagger-android-support:$dagger_version"

    annotationProcessor "com.google.dagger:dagger-android-processor:$dagger_version"
    annotationProcessor "com.google.dagger:dagger-compiler:$dagger_version"
}
```

## Component

Just add generated `ActivityModule.class` and `FragmentModule.class` into your Dagger's component

```java
@Singleton
@Component(modules = {
        AppModule.class,

        AndroidInjectionModule.class,
        AndroidSupportInjectionModule.class,

        ActivityModule.class,
        FragmentModule.class,
        ViewModelModule.class
})
public interface AppComponent {
    void inject(MainApplication application);

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);
        AppComponent build();
    }
}
```

## Setup Application

Just annotate your application with `@InjectApplication` givin your Component, 
then call `DaggerAutoInject.init(this);`

Don't forget to implement `HasActivityInjector` as follow

```java
@InjectApplication(component = AppComponent.class)
public class MainApplication extends Application implements HasActivityInjector {

    @Inject
    DispatchingAndroidInjector<Activity> activityAndroidInjector;

    @Override
    public void onCreate() {
        super.onCreate();
        
        final AppComponent appComponent = DaggerAppComponent.builder()
                        .application(this)
                        .build();
        
        DaggerAutoInject.init(this, appComponent);
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return activityAndroidInjector;
    }
}
```

## Inject Activity

```java
@InjectActivity
public class MainActivity extends AppCompatActivity {

    @Inject
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        sharedPreferences.edit()
                 .putString("androidVerions", "O")
                 .apply();
    }

}
```

## Inject Fragment
Just add `@InjectFragment` to your fragment

```java
@InjectFragment
public class MainFragment extends Fragment {

    @Inject
    SharedPreferences sharedPreferences;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        System.out.println(sharedPreferences.getAll());
    }
}
```

Then in your Activity / BaseActivity, implements `HasSupportFragmentInjector`

```java
public class BaseActivity extends AppCompatActivity implements HasSupportFragmentInjector {

    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingFragmentInjector;

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingFragmentInjector;
    }
}
```

## Inject ViewModel (for AAC)

```Java
@InjectViewModel
public class MainViewModel extends ViewModel {
}
```

# Credits

* Author: Florent Champigny
* Editor: WindSekirun (DongGil, Seo)
# License

    Copyright 2017 florent37, Inc.
    Copyright 2018 WindSekirun (DongGil, Seo)
   

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
