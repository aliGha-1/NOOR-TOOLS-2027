package com.noortools2027;

import android.app.Activity;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.text.InputType;
import android.util.TypedValue;

import java.security.MessageDigest;

public class MainActivity extends Activity {

    private LinearLayout root;
    private SharedPreferences preferences;

    private final int blue = Color.rgb(5, 25, 65);
    private final int lightBlue = Color.rgb(18, 58, 120);
    private final int gold = Color.rgb(212, 175, 55);
    private final int white = Color.WHITE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = getSharedPreferences(
            "noor_security", MODE_PRIVATE
        );

        getWindow().setStatusBarColor(blue);
        getWindow().setNavigationBarColor(blue);

        showWelcome();
    }

    private int dp(float value) {
        return (int) TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            value,
            getResources().getDisplayMetrics()
        );
    }

    private GradientDrawable roundedBackground(
        int color, int radius
    ) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(dp(radius));
        return drawable;
    }

    private void setupScreen() {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(blue);

        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setPadding(dp(24), dp(24), dp(24), dp(24));
        root.setBackgroundColor(blue);

        scroll.addView(root);
        setContentView(scroll);
    }

    private TextView text(
        String value, int size, int color
    ) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(size);
        view.setTextColor(color);
        view.setGravity(Gravity.CENTER);
        view.setPadding(dp(8), dp(10), dp(8), dp(10));
        return view;
    }

    private void addLogo(int size) {
        ImageView logo = new ImageView(this);

        int imageId = getResources().getIdentifier(
            "noor_tools_icon",
            "drawable",
            getPackageName()
        );

        if (imageId != 0) {
            logo.setImageResource(imageId);
            logo.setScaleType(ImageView.ScaleType.FIT_CENTER);

            LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                    dp(size), dp(size)
                );

            params.gravity = Gravity.CENTER;
            params.bottomMargin = dp(12);

            root.addView(logo, params);
        } else {
            TextView fallback = text("N", 76, gold);
            fallback.setTypeface(Typeface.DEFAULT_BOLD);
            root.addView(fallback);
        }
    }

    private void showWelcome() {
        setupScreen();

        View spacer = new View(this);
        root.addView(
            spacer,
            new LinearLayout.LayoutParams(
                1, dp(20)
            )
        );

        addLogo(190);

        TextView title = text(
            "NOOR TOOLS 2027", 28, white
        );
        title.setTypeface(Typeface.DEFAULT_BOLD);
        root.addView(title);

        root.addView(
            text("أدواتك.. في مكان واحد", 17, gold)
        );

        root.addView(
            text("تصميم وتطوير: علي غريب", 15, white)
        );

        root.postDelayed(() -> {
            if (preferences.contains("password_hash")) {
                showLogin();
            } else {
                showCreatePassword();
            }
        }, 1800);
    }

    private EditText passwordField(String hint) {
        EditText field = new EditText(this);
        field.setSingleLine(true);
        field.setHint(hint);
        field.setHintTextColor(Color.LTGRAY);
        field.setTextColor(white);

        field.setInputType(
            InputType.TYPE_CLASS_TEXT |
            InputType.TYPE_TEXT_VARIATION_PASSWORD
        );

        field.setPadding(
            dp(16), dp(10), dp(16), dp(10)
        );

        field.setBackground(
            roundedBackground(lightBlue, 12)
        );

        LinearLayout.LayoutParams params =
            new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            );

        params.setMargins(0, dp(6), 0, dp(6));
        field.setLayoutParams(params);

        return field;
    }

    private Button button(String label) {
        Button b = new Button(this);
        b.setText(label);
        b.setTextColor(blue);
        b.setTextSize(16);
        b.setTypeface(Typeface.DEFAULT_BOLD);

        b.setBackgroundTintList(
            ColorStateList.valueOf(gold)
        );

        LinearLayout.LayoutParams params =
            new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            );

        params.setMargins(0, dp(10), 0, dp(6));
        b.setLayoutParams(params);

        return b;
    }

    private void showCreatePassword() {
        setupScreen();
        addLogo(110);

        root.addView(
            text("إنشاء كلمة المرور", 27, gold)
        );

        root.addView(text(
            "أنشئ كلمة مرور خاصة بك واحتفظ بها في مكان آمن.",
            16, white
        ));

        EditText first =
            passwordField("كلمة المرور الجديدة");

        EditText second =
            passwordField("تأكيد كلمة المرور");

        root.addView(first);
        root.addView(second);

        Button save = button("حفظ ومتابعة");
        root.addView(save);

        save.setOnClickListener(v -> {
            String p1 = first.getText().toString();
            String p2 = second.getText().toString();

            if (p1.length() < 6) {
                toast("كلمة المرور يجب أن تكون 6 أحرف على الأقل");
                return;
            }

            if (!p1.equals(p2)) {
                toast("كلمتا المرور غير متطابقتين");
                return;
            }

            String hash = hashPassword(p1);

            if (hash == null) {
                toast("تعذر حفظ كلمة المرور");
                return;
            }

            preferences.edit()
                .putString("password_hash", hash)
                .apply();

            showHome();
        });
    }

    private void showLogin() {
        setupScreen();
        addLogo(125);

        root.addView(
            text("NOOR TOOLS 2027", 28, gold)
        );

        root.addView(
            text("أدخل كلمة المرور للمتابعة", 17, white)
        );

        EditText password = passwordField("كلمة المرور");
        root.addView(password);

        Button login = button("دخول");
        root.addView(login);

        login.setOnClickListener(v -> {
            String entered = hashPassword(
                password.getText().toString()
            );

            String saved = preferences.getString(
                "password_hash", ""
            );

            if (entered != null && entered.equals(saved)) {
                showHome();
            } else {
                toast("كلمة المرور غير صحيحة");
            }
        });
    }

    private void showHome() {
        setupScreen();
        addLogo(145);

        root.addView(
            text("NOOR TOOLS 2027", 29, gold)
        );

        root.addView(
            text("مرحبًا بك في تطبيق نور تولز", 19, white)
        );

        root.addView(
            text("تصميم وتطوير: علي غريب", 18, gold)
        );

        root.addView(
            text("للتواصل: 01559330658", 18, white)
        );

        Button change = button("تغيير كلمة المرور");
        root.addView(change);

        change.setOnClickListener(
            v -> showChangePassword()
        );
    }

    private void showChangePassword() {
        setupScreen();
        addLogo(90);

        root.addView(
            text("تغيير كلمة المرور", 25, gold)
        );

        EditText oldPassword =
            passwordField("كلمة المرور الحالية");

        EditText newPassword =
            passwordField("كلمة المرور الجديدة");

        root.addView(oldPassword);
        root.addView(newPassword);

        Button save = button("حفظ التغيير");
        root.addView(save);

        save.setOnClickListener(v -> {
            String oldHash = hashPassword(
                oldPassword.getText().toString()
            );

            String savedHash = preferences.getString(
                "password_hash", ""
            );

            String newValue =
                newPassword.getText().toString();

            if (oldHash == null ||
                !oldHash.equals(savedHash)) {
                toast("كلمة المرور الحالية غير صحيحة");
                return;
            }

            if (newValue.length() < 6) {
                toast(
                    "كلمة المرور الجديدة يجب أن تكون 6 أحرف على الأقل"
                );
                return;
            }

            String newHash = hashPassword(newValue);

            if (newHash != null) {
                preferences.edit()
                    .putString("password_hash", newHash)
                    .apply();

                toast("تم تغيير كلمة المرور");
                showHome();
            }
        });
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest =
                MessageDigest.getInstance("SHA-256");

            byte[] bytes = digest.digest(
                password.getBytes("UTF-8")
            );

            StringBuilder result = new StringBuilder();

            for (byte b : bytes) {
                result.append(
                    String.format("%02x", b & 0xff)
                );
            }

            return result.toString();

        } catch (Exception e) {
            return null;
        }
    }

    private void toast(String message) {
        Toast.makeText(
            this, message, Toast.LENGTH_SHORT
        ).show();
    }
}
