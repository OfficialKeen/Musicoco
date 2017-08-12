package com.duan.musicoco.main.leftnav;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.duan.musicoco.R;
import com.duan.musicoco.app.interfaces.ThemeChangeable;
import com.duan.musicoco.app.interfaces.ViewVisibilityChangeable;
import com.duan.musicoco.db.DBMusicocoController;
import com.duan.musicoco.main.MainActivity;
import com.duan.musicoco.preference.AppPreference;
import com.duan.musicoco.preference.ThemeEnum;
import com.duan.musicoco.util.ColorUtils;

/**
 * Created by DuanJiaNing on 2017/8/10.
 */

public class LeftNavigationController implements
        ViewVisibilityChangeable,
        NavigationView.OnNavigationItemSelectedListener,
        ThemeChangeable {

    private final Activity activity;
    private final AppPreference appPreference;
    protected DBMusicocoController dbController;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageWallController imageWallController;

    public LeftNavigationController(Activity activity, AppPreference appPreference) {
        this.activity = activity;
        this.appPreference = appPreference;
        this.imageWallController = new ImageWallController(activity, appPreference);
    }

    public void initViews() {
        drawerLayout = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) activity.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        imageWallController.initViews(navigationView);
        initDaytimeOrNightMode();
    }

    private void initDaytimeOrNightMode() {
        Menu menu = navigationView.getMenu();
        MenuItem item = menu.findItem(R.id.setting_night_mode);

        // 0 日间时应该显示的
        // 1 夜间时应该显示的
        Drawable[] ds = new Drawable[2];
        String[] ts = new String[2];

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ds[0] = activity.getDrawable(R.drawable.ic_night);
        } else {
            ds[0] = activity.getResources().getDrawable(R.drawable.ic_night);
        }
        ts[0] = activity.getString(R.string.setting_night_mode);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ds[1] = activity.getDrawable(R.drawable.ic_daytime);
        } else {
            ds[1] = activity.getResources().getDrawable(R.drawable.ic_daytime);
        }
        ts[1] = activity.getString(R.string.setting_daytime_mode);

        Drawable icon;
        String title;
        ThemeEnum theme = appPreference.getTheme();
        if (theme == ThemeEnum.WHITE || theme == ThemeEnum.VARYING) {
            icon = ds[0];
            title = ts[0];
        } else {
            icon = ds[1];
            title = ts[1];
        }

        item.setIcon(icon);
        item.setTitle(title);
    }

    public void initData(DBMusicocoController dbController) {
        this.dbController = dbController;

        imageWallController.initData(dbController);
        initImageWall();

    }

    private void initImageWall() {

        // FIXME null
        navigationView.post(new Runnable() {
            @Override
            public void run() {
                ImageView iv = (ImageView) navigationView.findViewById(R.id.main_left_nav_image);
                iv.post(new Runnable() {
                    @Override
                    public void run() {
                        updateImageWall();
                    }
                });
            }
        });
    }

    public void updateImageWall() {
        imageWallController.updateImageWall();
    }

    public boolean onBackPressed() {
        if (visible()) {
            hide();
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void show() {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    @Override
    public void hide() {
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    public boolean visible() {
        return drawerLayout.isDrawerOpen(GravityCompat.START);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.setting_scan: // 文件扫描

                break;
            case R.id.setting_sleep: // 睡眠定时

                break;
            case R.id.setting_image_wall: // 照片墙

                break;
            case R.id.setting_play_ui: // 播放界面设置

                break;
            case R.id.setting_theme_color_custom: // 主题色

                break;
            case R.id.setting_night_mode: // 夜间模式
                handleModeSwitch(item);
                break;
            case R.id.setting_set: // 设置

                break;
            case R.id.setting_quit: // 退出

                break;
            default:
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

    private void handleModeSwitch(MenuItem item) {
        ThemeEnum theme = appPreference.getTheme();

        Drawable icon;
        String title;

        if (theme == ThemeEnum.WHITE || theme == ThemeEnum.VARYING) { // 切换到 夜间模式
            theme = ThemeEnum.DARK;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                icon = activity.getDrawable(R.drawable.ic_daytime);
            } else {
                icon = activity.getResources().getDrawable(R.drawable.ic_daytime);
            }
            title = activity.getString(R.string.setting_daytime_mode);
        } else { // 切换到 白天模式
            theme = ThemeEnum.WHITE;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                icon = activity.getDrawable(R.drawable.ic_night);
            } else { // 切换到 白天模式
                icon = activity.getResources().getDrawable(R.drawable.ic_night);
            }
            title = activity.getString(R.string.setting_night_mode);
        }

        item.setIcon(icon);
        item.setTitle(title);

        appPreference.updateTheme(theme);
        ((MainActivity) activity).switchThemeMode(theme);

    }

    @Override
    public void themeChange(ThemeEnum themeEnum, int[] colors) {
        ThemeEnum th = appPreference.getTheme();
        int[] cs = ColorUtils.get10ThemeColors(activity, th);
        int mainBC = cs[3];
        int mainTC = cs[5];
        int vicTC = cs[6];
        int accentC = cs[2];

        navigationView.setItemTextColor(ColorStateList.valueOf(mainTC));
        navigationView.setBackgroundColor(mainBC);

        Menu menu = navigationView.getMenu();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            for (int i = 0; i < menu.size(); i++) {
                MenuItem item = menu.getItem(0);
                Drawable icon = item.getIcon();
                if (icon != null) {
                    icon.setTint(accentC);
                }
            }
        }
    }
}