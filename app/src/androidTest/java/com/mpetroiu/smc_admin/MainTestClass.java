package com.mpetroiu.smc_admin;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SdkSuppress;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.*;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

import java.util.Random;

import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class MainTestClass {

    private static final String TAG = "MainTestClass";
    private static final String APP_PACKAGE = "com.mpetroiu.smc";
    private static final int LAUNCH_TIMEOUT = 5000;
    private FirebaseAuth mAuth;
    private UiDevice mDevice;
    private Context context;

    private String randomInt() {
        return String.valueOf(((new Random()).nextInt(100000)));
    }


    @Test
    public void testA1_startApp() {
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        mDevice.pressHome();

        final String launcherPackage = mDevice.getLauncherPackageName();
        assertThat(launcherPackage, notNullValue());
        mDevice.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), LAUNCH_TIMEOUT);

        context = InstrumentationRegistry.getContext();
        final Intent intent = context.getPackageManager()
                .getLaunchIntentForPackage(APP_PACKAGE);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    @Test
    public void testA2_verifyLogOut() throws UiObjectNotFoundException {
        testA1_startApp();
        mDevice.waitForIdle();

        UiObject navButton = mDevice.findObject(new UiSelector()
                .className("android.widget.ImageButton")
                .descriptionContains("Navigate up"));

        UiObject logoutBtn = mDevice.findObject(new UiSelector()
                .className("android.widget.CheckedTextView")
                .resourceId("com.mpetroiu.smc:id/design_menu_item_text")
                .text("Logout"));

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            navButton.click();
            mDevice.waitForIdle();
            logoutBtn.click();
            mDevice.waitForIdle();
        } else {
            mDevice.waitForIdle();
        }
    }

    @Test
    public void testA3_goToLoginOptions() throws UiObjectNotFoundException {
        testA2_verifyLogOut();

        UiObject loginBtn = mDevice.findObject(new UiSelector()
                .className("android.widget.Button")
                .resourceId("com.mpetroiu.smc:id/loginBtn")
                .text("LOGIN"));

        UiObject emailLogin = mDevice.findObject(new UiSelector()
                .className("android.widget.Button")
                .resourceId("com.mpetroiu.smc:id/emailLogin")
                .text("Login with email"));

        mDevice.waitForIdle();
        loginBtn.click();
        assertTrue("Account was not deleted", emailLogin.exists());
        mDevice.waitForIdle();
    }

    @TestS
    public void testA4_goToLogin() throws UiObjectNotFoundException {
        testA3_goToLoginOptions();
        mDevice.waitForIdle();

        UiObject emailLogin = mDevice.findObject(new UiSelector()
                .className("android.widget.Button")
                .resourceId("com.mpetroiu.smc:id/emailLogin")
                .text("Login with email"));

        UiObject verifyLogin = mDevice.findObject(new UiSelector()
                .className("android.widget.TextView")
                .resourceId("com.mpetroiu.smc:id/welcome")
                .text("Welcome"));

        emailLogin.click();
        assertTrue("Login with email did not open", verifyLogin.exists());
        mDevice.waitForIdle();

    }

    @Test
    public void testA5_EditTextAreCreated() throws UiObjectNotFoundException {
        testA4_goToLogin();
        mDevice.waitForIdle();

        UiObject emailInput = mDevice.findObject(new UiSelector()
                .className("android.widget.EditText")
                .resourceId("com.mpetroiu.smc:id/inputEmail")
                .text("Email"));

        UiObject passInput = mDevice.findObject(new UiSelector()
                .className("android.widget.EditText")
                .resourceId("com.mpetroiu.smc:id/inputPass")
                .text("Password"));

        assertNotNull(emailInput);
        assertNotNull(passInput);
    }

    @Test
    public void testA6_LoginWithEmail() throws UiObjectNotFoundException {
        String email = "moise@yahoo.com";
        String pass = "123456";

        testA4_goToLogin();
        mDevice.waitForIdle();

        UiObject emailInput = mDevice.findObject(new UiSelector()
                .className("android.widget.EditText")
                .resourceId("com.mpetroiu.smc:id/etEmaill")
                .text("Email"));

        UiObject passInput = mDevice.findObject(new UiSelector()
                .className("android.widget.EditText")
                .resourceId("com.mpetroiu.smc:id/etPass")
                .text("Password"));

        UiObject signInBtn = mDevice.findObject(new UiSelector()
                .className("android.widget.Button")
                .resourceId("com.mpetroiu.smc:id/signIn")
                .text("Login"));

        emailInput.clearTextField();
        passInput.clearTextField();
        assertTrue("Email EditText not found ", emailInput.exists());

        emailInput.setText(email);
        mDevice.waitForIdle();

        assertTrue("Password EditText not found ", passInput.exists());

        passInput.setText(pass);
        mDevice.waitForIdle();

        signInBtn.click();
        mDevice.waitForIdle();
    }

    @Test
    public void testA7_CreateAcc() throws UiObjectNotFoundException {
        String name = "nume" + randomInt();
        String email = "user" + randomInt() + "@example.com";
        String pass = "password" + randomInt();

        testA3_goToLoginOptions();
        mDevice.waitForIdle();

        UiObject createAccLink = mDevice.findObject(new UiSelector()
                .className("android.widget.TextView")
                .resourceId("com.mpetroiu.smc:id/newUser")
                .text("Create an account"));

        UiObject nameInput = mDevice.findObject(new UiSelector()
                .className("android.widget.smc")
                .resourceId("com.mpetroiu.uniapplication:id/username")
                .text("Name"));

        UiObject emailInput = mDevice.findObject(new UiSelector()
                .className("android.widget.EditText")
                .resourceId("com.mpetroiu.smc:id/inputEmail")
                .text("Email"));
        UiObject verifyEmailInput = mDevice.findObject(new UiSelector()
                .className("android.widget.EditText")
                .resourceId("com.mpetroiu.smc:id/inputEmail")
                .text("Confirm Email"));

        UiObject passInput = mDevice.findObject(new UiSelector()
                .className("android.widget.EditText")
                .resourceId("com.mpetroiu.smc:id/inputPass")
                .text("Password"));

        UiObject createAccBtn = mDevice.findObject(new UiSelector()
                .className("android.widget.Button")
                .resourceId("com.mpetroiu.smc:id/signUp")
                .text("Create account"));

        createAccLink.click();
        mDevice.waitForIdle();

        assertTrue("Name EditText not found ", nameInput.exists());
        nameInput.setText(name);
        mDevice.waitForIdle();

        assertTrue("Email EditText not found ", emailInput.exists());
        emailInput.setText(email);
        mDevice.waitForIdle();

        assertTrue("Password EditText not found ", passInput.exists());
        passInput.setText(pass);
        mDevice.waitForIdle();

        createAccBtn.click();
        mDevice.waitForIdle();

    }

    @Test
    public void testA8_signInWithGoogle() throws UiObjectNotFoundException {
        testA3_goToLoginOptions();
        mDevice.waitForIdle();

        UiObject withGoogle = mDevice.findObject(new UiSelector()
                .className("android.widget.Button")
                .resourceId("com.mpetroiu.smc:id/googleLogin")
                .text("Login with Google"));

        UiObject chooseGoogleAcc = mDevice.findObject(new UiSelector()
                .className("android.widget.TextView")
                .resourceId("com.google.android.gms:id/title")
                .text("Choose account for SMC-Admin"));

        UiObject selectAcc = mDevice.findObject(new UiSelector()
                .packageName("com.google.android.gms")
                .className("android.widget.LinearLayout")
                .index(0));

        withGoogle.click();
        mDevice.waitForIdle();

        assertTrue("Login with Google Failed", chooseGoogleAcc.exists());
        selectAcc.click();
        mDevice.waitForIdle();
    }

    @Test
    public void testB1_verifyNavBar() throws UiObjectNotFoundException {
        testA6_LoginWithEmail();
        mDevice.waitForIdle();

        UiObject navButton = mDevice.findObject(new UiSelector()
                .className("android.widget.ImageButton")
                .descriptionContains("Navigate up"));

        UiObject actionTV = mDevice.findObject(new UiSelector()
                .className("android.widget.TextView")
                .text("Actions"));

        assertTrue("Navigation drawer was not opened", actionTV.exists());
        navButton.click();
        mDevice.waitForIdle();
    }
}

