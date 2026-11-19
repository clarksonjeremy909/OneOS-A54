# OneOS A54 – Samsung Galaxy A54 5G için OxygenOS 16 / Dual Android

<p align="center">
  <img alt="Unofficial fork" src="https://img.shields.io/badge/fork-unofficial-5B3FCB?style=flat-square&labelColor=2B3445">
  <img alt="Status: experimental" src="https://img.shields.io/badge/status-experimental-5B3FCB?style=flat-square&labelColor=2B3445">
  <img alt="Galaxy A54 5G SM-A546" src="https://img.shields.io/badge/Galaxy-A54%205G%20(SM--A546)-1C5FD6?style=flat-square&labelColor=2B3445">
  <img alt="One UI 8" src="https://img.shields.io/badge/One%20UI-8-1C5FD6?style=flat-square&labelColor=2B3445">
  <img alt="Android 16" src="https://img.shields.io/badge/Android-16-1C5FD6?style=flat-square&labelColor=2B3445">
  <img alt="OxygenOS 16" src="https://img.shields.io/badge/OxygenOS-16-1C5FD6?style=flat-square&labelColor=2B3445">
  <img alt="Root required" src="https://img.shields.io/badge/root-required-1C5FD6?style=flat-square&labelColor=2B3445">
  <img alt="Türkçe / English" src="https://img.shields.io/badge/UI-TR%20%2B%20EN-10B981?style=flat-square&labelColor=2B3445">
  <img alt="Apache 2.0" src="https://img.shields.io/badge/license-Apache%202.0-10B981?style=flat-square&labelColor=2B3445">
</p>

**OneOS A54**, [OneOS](https://github.com/fahadalijaved/OneOS) projesinin Samsung Galaxy A54 5G (SM-A546) için **gayriresmi, değiştirilmiş bir çatalıdır**. One UI 8 / Android 16 üzerinde, telefonu flaşlamadan OxygenOS 16 konuk sistemine geçiş (dual boot benzeri bir değiştirici) sunar. Türkçe ve İngilizce APK vardır.

> [!WARNING]
> **Resmi OneOS değildir.** Deneyseldir ve telefonunuzun verisini bozabilir. Root (KernelSU, Magisk veya APatch) gerekir. Bu yapımı kendi riskinizle kurun; Samsung, Google, OnePlus veya orijinal OneOS yapımcısı desteklemez. Önce yedek alın.

Kaynak, upstream **OneOS 0.5.24** üzerine kuruludur. Değişiklik listesi: [ONEOS_A54_CHANGES.md](ONEOS_A54_CHANGES.md). Lisans: [Apache License 2.0](LICENSE).

---

## Türkçe

### Özellikler

- **Türkçe ve İngilizce APK** — `OneOS-A54-*-tr.apk` arayüzü Türkçe zorlar; `OneOS-A54-*-en.apk` varsayılan olarak İngilizcedir, telefon dili Türkçeyse Türkçe açılır. Uygulama adı **OneOS A54**.
- **OxygenOS 16 konuk sistemi** — Galaxy A54 5G, One UI 8 / Android 16 üzerinde, desteklenen resmi OneOS imajlarından OxygenOS 16’ya geçiş.
- **Kasma azaltma (her konuk ROM)** — yalnızca OxygenOS için değil. İlk oturumda uygulama derlemesi (`dexopt` / `speed`) açık; “Her konuk sistemde kasmayı azalt” (varsayılan açık, Ayarlar’dan kapanır): animasyonlar yarı hız, arka plan süreç sınırı, hafif günlük kısma, güvenli I/O ayarları. Hepsi kaydedilir ve One UI’ye dönünce geri alınır. “Isınınca soğut” (45 °C işlemci freni) varsayılan olarak kapalıdır. Konuk uygulamalar kapatılmaz.
- **Dosyaları aktar** — Bring apps içinde fotoğraf, video, İndirilenler, belgeler, müzik (klasör paylaşımı), APK kopyası ve uygulama getirme.
- **Konuk açıkken One UI uygulamalarını dondur** — varsayılan **kapalı**. Açılırsa, konuk ROM çalışırken gerekli olmayan One UI uygulamaları geri alınabilir biçimde duraklatılır. One UI’ye dönüşte, uygulama açılışında ve açılışta çözülürler. Ayarlar’da **Dondurulan uygulamaları geri aç** düğmesi vardır.
- **Canavar modu** — isteğe bağlı, varsayılan kapalı; daha güçlü ama yine de geri alınabilir kasma azaltma (saat yükseltme veya ısıl sınır kaldırma yok). Yalnızca “kasmayı azalt” da açıkken uygulanır.

### Gereksinimler

- Samsung Galaxy A54 5G (**SM-A546** / a54x, Exynos 1380). Galaxy A73 5G profili de eklendi (doğrulanmış kimlikler, **test edilmedi**).
- **One UI 8**, **Android 16**
- Çalışan **root** (KernelSU, Magisk veya APatch; geçici root da kullanılabilir ama risklidir)
- OxygenOS 16 konuk imajı OneOS tarafından indirilir (set dosyaları çalışma anında gelir)

Bu yapım özellikle **Galaxy A54 5G** için hazırlanmıştır. Telefonunuz bir A54 değilse yine de deneyebilirsiniz; ancak çalışacağı garanti edilmez ve risk size aittir.

### Kurulum

1. **Resmi OneOS’u kaldırın.** Bu çatalın imzası resmi uygulamadan farklıdır; üstüne kurulmaz. Önceki **OneOS A54** sürümünün üzerine kurulabilir (aynı imza). Türkçe ve İngilizce APK aynı paket adını kullanır; biri diğerinin üzerine kurulur.
2. [Releases](https://github.com/clarksonjeremy909/OneOS-A54/releases) sayfasından `OneOS-A54-0.5.25-tr.apk` (Türkçe) veya `OneOS-A54-0.5.25-en.apk` (İngilizce) dosyasını indirin. SHA-256 özeti sürüm notlarındadır.
3. APK’yı kurun, root izni verin ve OneOS A54 içinden OxygenOS 16’yı hazırlayın.

### Güncellemeler

Uygulama, resmi OneOS güncelleme bildirimine bakmaz. Yeni sürümler bu repodaki [`version.json`](version.json) dosyasından gelir; ana ekranda en fazla altı saatte bir kontrol edilir. Türkçe APK Türkçe paketini, İngilizce APK İngilizce paketini önerir. Yeni bir sürüm yayınlamak için [UPDATING.md](UPDATING.md) dosyasına bakın.

### Riskler

- Root, Knox / Samsung Pay ve benzeri özellikleri etkileyebilir; garanti durumunuz değişebilir.
- OneOS deneyseldir: çekirdek ve sistem bölümlerine root olarak dokunur. Orijinal projenin uyardığı gibi, yedek alın, mümkünse geçişten önce ekran kilidini kaldırın, yalnızca sıfırlamayı göze alabileceğiniz bir telefonda deneyin.
- **Galaxy A54 5G’de test edildi:** OxygenOS 16 açılıyor; ilk açılıştan 10–20 dk sonra akıcı, kasmalar azalıyor (ilk açılışta ısınma normal). Diğer A serisi ve başka telefonlar denenmedi, çalışacağı garanti değil. Klasör paylaşımı canlıdır: konuk sistemde sildiğiniz dosya One UI tarafında da silinebilir.
- Sıfır kasma veya her uygulamada sorunsuz çalışma garanti edilmez.

### Geliştiriciler

- **Miraç Aytaç** ([@clarksonjeremy909](https://github.com/clarksonjeremy909)) — OneOS A54 mod geliştiricisi
- **Grok Bot** — yapay zeka asistanı
- **[fahadalijaved](https://github.com/fahadalijaved)** — orijinal [OneOS](https://github.com/fahadalijaved/OneOS) geliştiricisi

---

## English

**OneOS A54** is an **unofficial modified fork** of [OneOS](https://github.com/fahadalijaved/OneOS) made specifically for the Samsung Galaxy A54 5G (SM-A546). It is a dual-boot-style system switcher: on One UI 8 / Android 16 you can start an OxygenOS 16 guest without flashing a ROM. There is a Turkish APK and an English APK.

This tree is based on upstream **OneOS 0.5.24**. See [ONEOS_A54_CHANGES.md](ONEOS_A54_CHANGES.md) for the A54 changes. Licensed under the [Apache License 2.0](LICENSE).

### Features

- **Turkish and English APKs** — `OneOS-A54-*-tr.apk` forces a Turkish UI; `OneOS-A54-*-en.apk` defaults to English and uses Turkish when the phone language is Turkish. The app is branded **OneOS A54**.
- **OxygenOS 16 guest** on a Galaxy A54 5G running One UI 8 / Android 16.
- **Lag reductions for every guest ROM**, not only OxygenOS: first-session dexopt (`speed`) stays on; “Reduce lag in every guest system” (default on, can be turned off in Settings) halves animation speed, caps cached background processes, trims a little guest activity logging, and applies reversible I/O tweaks (readahead 128 KB if higher, scheduler `none` only when the kernel lists it). All of those are saved and restored on return to One UI. “Cool down when hot” (45 °C CPU throttle) is off by default. Guest packages are never disabled.
- **File transfer** in Bring apps: photos, videos, Downloads, documents, music (folder share), APK copy, and app bring.
- **Freeze One UI apps while any guest is open** (default **off**). If you turn it on, non-essential One UI apps are reversibly suspended. They are thawed on return to One UI, when OneOS opens, and on boot. Settings has an **Unfreeze apps** button.
- **Beast mode** — optional, default off; stronger but still reversible lag reductions (no overclocking or thermal-limit removal). Applied only while “Reduce lag” is also on.

### Requirements

- Samsung Galaxy A54 5G (**SM-A546** / a54x, Exynos 1380). A Galaxy A73 5G profile is also included (verified identifiers, **untested**).
- **One UI 8**, **Android 16**
- Working **root** (KernelSU, Magisk or APatch; temporary root is possible but risky)
- The OxygenOS 16 guest image is provided by OneOS (package sets are downloaded at runtime)

This build was made specifically for the **Galaxy A54 5G**. If your phone is not an A54 you can still try it, but it is not guaranteed to work — at your own risk.

### Install

1. **Uninstall official OneOS first.** This fork is signed with a different key and will not install over it. You *can* update over a previous **OneOS A54** build (same key). The Turkish and English APKs share the same package name, so installing one replaces the other.
2. Download `OneOS-A54-0.5.25-tr.apk` (Turkish) or `OneOS-A54-0.5.25-en.apk` (English) from [Releases](https://github.com/clarksonjeremy909/OneOS-A54/releases). The SHA-256 digest is in the release notes.
3. Install the APK, grant root, and prepare OxygenOS 16 from inside OneOS A54.

### Updates

The app does not follow official OneOS update notices. It checks this repo’s [`version.json`](version.json) at most every six hours from the home screen. The Turkish build offers the Turkish APK, and the English build offers the English APK. To publish a new build, see [UPDATING.md](UPDATING.md).

### Risks and disclaimer

- Root may affect Knox, Samsung Pay and related features, and can change your warranty situation.
- OneOS is experimental and uses root on core system parts. Back up first, remove the screen lock before switching if you can, and only use a phone you can afford to factory-reset. See the warnings in the [upstream project](https://github.com/fahadalijaved/OneOS).
- **Tested on Galaxy A54 5G:** OxygenOS 16 boots; smooth after 10–20 min from first boot, lag reduced (warming up on first boot is normal). Other A-series phones and other devices are untested and not guaranteed. Folder share is live: deleting a file in the guest can delete it on One UI too.
- Smoother performance is expected, not guaranteed. Use this software at your own risk. Not affiliated with or supported by Samsung, Google, OnePlus, or the original OneOS author.

### Developers

- **Miraç Aytaç** ([@clarksonjeremy909](https://github.com/clarksonjeremy909)) — OneOS A54 mod developer
- **Grok Bot** — AI assistant
- **[fahadalijaved](https://github.com/fahadalijaved)** — original [OneOS](https://github.com/fahadalijaved/OneOS) developer

---

## License and credits

- [LICENSE](LICENSE) — Apache License 2.0 (same as upstream OneOS)
- [NOTICE](NOTICE) — required attribution, including this unofficial fork
- [CREDITS.md](CREDITS.md) — OneLineage, MysticGSI, OneUIProject, and other third-party work
- [ONEOS_A54_CHANGES.md](ONEOS_A54_CHANGES.md) — what this fork changes
- [UPDATING.md](UPDATING.md) — how to publish an update through `version.json`
- Upstream: [fahadalijaved/OneOS](https://github.com/fahadalijaved/OneOS)

Galaxy, One UI and Samsung are trademarks of Samsung Electronics; Android of Google LLC; OxygenOS of OnePlus. This project is not affiliated with any of them.
