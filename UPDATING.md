# OneOS A54 güncellemesi yayınlama / Publishing an update

Türkçe APK, İngilizce APK ve `version.json` aynı anda güncellenir. Uygulama resmi OneOS `update.json` dosyasına bakmaz; kendi kanalı şudur:

`https://raw.githubusercontent.com/clarksonjeremy909/OneOS-A54/main/version.json`

Kontrol en fazla altı saatte bir yapılır (ana ekran / Sistemler). `versionName` yalnızca `0.5.24.1` gibi noktayla ayrılmış sayılar olabilir (`0.5.24-a54.1` kabul edilmez). Sürüm etiketi (ör. `v0.5.24-a54.1`) serbesttir. `versionCode` yüklü kopyadan **büyük** olmalıdır.

---

## Türkçe

1. Kaynakta `versionCode` değerini artırın (şu an **169**) ve `versionName` değerini `AppUpdate` kurallarına uygun yazın (ör. `0.5.24.2` veya `0.5.25`).
2. Önce Türkçe, sonra İngilizce derleyin (`-Poneos.ui=tr` ve `-Poneos.ui=en`). Her dil için konuk yöneticisini (`scripts/build-guest-manager.sh --debug`) o dil parametresiyle yeniden üretin, ardından `assembleFullDebug -Poneos.series=s25 -Poneos.ui=...`.
3. APK’ları her zamanki OneOS A54 imza anahtarıyla (v1+v2+v3) imzalayın. Farklı bir anahtar, mevcut kurulumun üzerine güncellemeyi bozar.
4. GitHub’da yeni bir release açın (ör. `v0.5.24-a54.2`) ve imzalı dosyaları şu adlarla yükleyin:
   - `OneOS-A54-<sürüm>-tr.apk`
   - `OneOS-A54-<sürüm>-en.apk`
5. `main` dalındaki [`version.json`](version.json) dosyasını düzenleyin:
   - `versionCode`, `versionName`, `notes`
   - `apkTr` / `apkEn`: bu release’teki asset URL’leri (`https://github.com/clarksonjeremy909/OneOS-A54/releases/download/<etiket>/OneOS-A54-....apk`)
   - `bytesTr` / `bytesEn`: dosya boyutu (bayt)
   - `sha256Tr` / `sha256En`: küçük harfli SHA-256
6. `version.json` dosyasını `main`e işleyin. Uygulama birkaç saat içinde (veya uygulama yeniden açılınca, zorlanmış kontrolde hemen) yeni sürümü görür.

`applicationId` `com.fahadali.oneos` olarak kalır; Türkçe ve İngilizce paketler birbirinin üzerine kurulur. Her kanal yalnızca kendi dilindeki APK’yı önerir.

---

## English

1. Bump `versionCode` in the source (currently **169**) and set `versionName` to a dotted numeric value the parser accepts (for example `0.5.24.2` or `0.5.25`). Do not use a hyphenated name such as `0.5.24-a54.1`.
2. Build Turkish first, then English (`-Poneos.ui=tr` and `-Poneos.ui=en`). Rebuild the guest manager (`scripts/build-guest-manager.sh --debug`) with the same `oneos.ui` property before each host assemble (`assembleFullDebug -Poneos.series=s25 -Poneos.ui=...`).
3. Sign both APKs with the usual OneOS A54 keystore (v1+v2+v3). A different key will not install over existing copies.
4. Create a GitHub release (for example `v0.5.24-a54.2`) and upload the signed files as:
   - `OneOS-A54-<version>-tr.apk`
   - `OneOS-A54-<version>-en.apk`
5. Edit [`version.json`](version.json) on `main`:
   - `versionCode`, `versionName`, `notes`
   - `apkTr` / `apkEn`: the release asset URLs (`https://github.com/clarksonjeremy909/OneOS-A54/releases/download/<tag>/OneOS-A54-....apk`)
   - `bytesTr` / `bytesEn`: size in bytes
   - `sha256Tr` / `sha256En`: lowercase SHA-256
6. Commit `version.json` to `main`. The app picks it up within six hours, or sooner after a relaunch / forced check.

The `applicationId` stays `com.fahadali.oneos`, so the Turkish and English APKs replace each other. Each build only offers its own language APK.
