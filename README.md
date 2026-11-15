# 2048 — Native Android (Kotlin + Jetpack Compose)

ساخت کامل این پروژه: یک بازی ۲۰۴۸ واقعی اندرویدی، کامپایل‌شده در GitHub Actions.

## ساختار پروژه

| مسیر | توضیح |
|---|---|
| `app/src/main/java/.../core/GameEngine.kt` | موتور خالص بازی (حرکت، ادغام، امتیاز، پایان بازی) |
| `app/src/main/java/.../core/GameState.kt` | مدل تغییرناپذیر وضعیت بازی |
| `app/src/main/java/.../core/ScoreStorage.kt` | ذخیره‌ی رکورد با SharedPreferences |
| `app/src/main/java/.../ui/Game2048App.kt` | رابط کاربری کامل بازی با swipe |
| `app/src/main/java/.../ui/Overlays.kt` | صفحه‌های برد و باخت |
| `app/src/main/java/.../ui/theme/` | تم، رنگ‌ها و شکل‌های متریال ۳ |
| `app/src/test/java/.../GameEngineTest.kt` | تست‌های واحد موتور بازی |
| `.github/workflows/build.yml` | وُرک‌فلو ساخت APK (باید دستی ساخته شود — زیر) |

## ساخت APK در GitHub Actions

فایل `.github/workflows/build.yml` را با محتوای `docs/workflow-placeholder.yml` بسازید (کانکتور اجازه نوشتن مستقیم در `.github/workflows` را ندارد):

1. ریپو → **Add file → Create new file**
2. نام فایل: `.github/workflows/build.yml`
3. محتوای `docs/workflow-placeholder.yml` را کپی کنید
4. Commit

سپس هر push → تب **Actions** → اجرا → **Artifacts → app-debug-apk** → دانلود `app-debug.apk` و نصب روی گوشی.

## نکات فنی

- Kotlin 1.9.24، AGP 8.5.2، Gradle 8.7، Compose BOM 2024.06.00
- `gradle-wrapper.jar` در ریپو نیست؛ workflow از `gradle/actions/setup-gradle@v3` استفاده می‌کند که خودش wrapper را تولید می‌کند
- minSdk 24 (اندروید ۷ به بالا)، targetSdk 34

## تست

```
./gradlew testDebugUnitTest
```

۸ تست واحد برای موتور بازی: ادغام، امتیاز، تشخیص پایان بازی و برد.
