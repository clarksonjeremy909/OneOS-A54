# OneOS A54 – Samsung Galaxy A54 için OxygenOS 16 / Dual Android (Türkçe)

<p align="center">
  <img alt="Unofficial fork" src="https://img.shields.io/badge/fork-unofficial-5B3FCB?style=flat-square&labelColor=2B3445">
  <img alt="Status: experimental" src="https://img.shields.io/badge/status-experimental-5B3FCB?style=flat-square&labelColor=2B3445">
  <img alt="Galaxy A54 SM-A546" src="https://img.shields.io/badge/Galaxy-A54%20(SM--A546)-1C5FD6?style=flat-square&labelColor=2B3445">
  <img alt="One UI 8" src="https://img.shields.io/badge/One%20UI-8-1C5FD6?style=flat-square&labelColor=2B3445">
  <img alt="Android 16" src="https://img.shields.io/badge/Android-16-1C5FD6?style=flat-square&labelColor=2B3445">
  <img alt="OxygenOS 16" src="https://img.shields.io/badge/OxygenOS-16-1C5FD6?style=flat-square&labelColor=2B3445">
  <img alt="Root required" src="https://img.shields.io/badge/root-required-1C5FD6?style=flat-square&labelColor=2B3445">
  <img alt="Türkçe" src="https://img.shields.io/badge/UI-Türkçe-10B981?style=flat-square&labelColor=2B3445">
  <img alt="Apache 2.0" src="https://img.shields.io/badge/license-Apache%202.0-10B981?style=flat-square&labelColor=2B3445">
</p>

**OneOS A54**, [OneOS](https://github.com/fahadalijaved/OneOS) projesinin Samsung Galaxy A54 (SM-A546) için **gayriresmi, değiştirilmiş bir çatalıdır**. One UI 8 / Android 16 üzerinde, telefonu flaşlamadan OxygenOS 16 konuk sistemine geçiş (dual boot benzeri bir değiştirici) sunar. Arayüz Türkçedir.

> [!WARNING]
> **Resmi OneOS değildir.** Deneyseldir ve telefonunuzun verisini bozabilir. Root (KernelSU, Magisk veya APatch) gerekir. Bu yapımı kendi riskinizle kurun; Samsung, Google, OnePlus veya orijinal OneOS yapımcısı desteklemez. Önce yedek alın.

Kaynak, upstream **OneOS 0.5.23** üzerine kuruludur. Değişiklik listesi: [ONEOS_A54_CHANGES.md](ONEOS_A54_CHANGES.md). Lisans: [Apache License 2.0](LICENSE).

---

## Türkçe

### Özellikler

- **Türkçe arayüz** — uygulama adı **OneOS A54**; metinler varsayılan olarak Türkçedir (İngilizce `values/` içinde yedek durur).
- **OxygenOS 16 konuk sistemi** — Galaxy A54, One UI 8 / Android 16 üzerinde, desteklenen resmi OneOS imajlarından OxygenOS 16’ya geçiş.
- **Kasma azaltma** — ilk oturumda uygulamaların önceden derlenmesi (`dexopt` / `speed`); “Isınınca soğut” (45 °C işlemci freni) varsayılan olarak kapalıdır, Ayarlar’dan açılabilir.
- **Dosyaları aktar** — Bring apps içinde fotoğraf, video, İndirilenler, belgeler, müzik (klasör paylaşımı), APK kopyası ve uygulama getirme.
- **Konuk açıkken One UI uygulamalarını dondur** — varsayılan açık; OxygenOS çalışırken gerekli olmayan One UI uygulamaları geri alınabilir biçimde duraklatılır. “Dondurulanları şimdi geri aç” düğmesi ve bir muaf listesi vardır.

### Gereksinimler

- Samsung Galaxy A54 (**SM-A546** / a54x, Exynos 1380)
- **One UI 8**, **Android 16**
- Çalışan **root** (KernelSU, Magisk veya APatch; geçici root da kullanılabilir ama risklidir)
- OxygenOS 16 konuk imajı OneOS tarafından indirilir (set dosyaları çalışma anında gelir)

### Kurulum

1. **Resmi OneOS’u kaldırın.** Bu çatalın imzası resmi uygulamadan farklıdır; üstüne kurulmaz. Önceki **OneOS A54** sürümünün üzerine kurulabilir (aynı imza).
2. [Releases](https://github.com/clarksonjeremy909/OneOS-A54/releases) sayfasından `OneOS-A54-0.5.23-tr.apk` dosyasını indirin. SHA-256 özeti sürüm notlarındadır.
3. APK’yı kurun, root izni verin ve OneOS A54 içinden OxygenOS 16’yı hazırlayın.
4. Uygulama içinde resmi OneOS **Güncelle** banner’ı çıkarsa **basmayın**. Resmi paket bu imzayla yüklenmez ve kurulum hata verir.

### Riskler

- Root, Knox / Samsung Pay ve benzeri özellikleri etkileyebilir; garanti durumunuz değişebilir.
- OneOS deneyseldir: çekirdek ve sistem bölümlerine root olarak dokunur. Orijinal projenin uyardığı gibi, yedek alın, mümkünse geçişten önce ekran kilidini kaldırın, yalnızca sıfırlamayı göze alabileceğiniz bir telefonda deneyin.
- Bu A54 yapımı telefonda kapsamlı bir testten geçmemiştir. İlk oturum 10–20 dakika ısınıp yavaşlayabilir. Klasör paylaşımı canlıdır: konuk sistemde sildiğiniz dosya One UI tarafında da silinebilir.
- Sıfır kasma veya her uygulamada sorunsuz çalışma garanti edilmez.

### Geliştiriciler

- **Miraç Aytaç** ([@clarksonjeremy909](https://github.com/clarksonjeremy909)) — OneOS A54 mod geliştiricisi
- **Grok bot 2 (miraç)** — yapay zeka asistanı
- **[fahadalijaved](https://github.com/fahadalijaved)** — orijinal [OneOS](https://github.com/fahadalijaved/OneOS) geliştiricisi

---

## English

**OneOS A54** is an **unofficial modified fork** of [OneOS](https://github.com/fahadalijaved/OneOS) for the Samsung Galaxy A54 (SM-A546). It is a dual-boot-style system switcher: on One UI 8 / Android 16 you can start an OxygenOS 16 guest without flashing a ROM. The app UI is Turkish by default.

This tree is based on upstream **OneOS 0.5.23**. See [ONEOS_A54_CHANGES.md](ONEOS_A54_CHANGES.md) for the A54 changes. Licensed under the [Apache License 2.0](LICENSE).

### Features

- **Turkish UI**, branded **OneOS A54** (English strings kept as a backup in `values/`).
- **OxygenOS 16 guest** on a Galaxy A54 running One UI 8 / Android 16.
- **Performance tweaks:** first-session dexopt (`speed`); the “cool down when hot” CPU throttle from 45 °C is off by default and can be turned back on in Settings.
- **File transfer** in Bring apps: photos, videos, Downloads, documents, music (folder share), APK copy, and app bring.
- **Freeze One UI apps while the guest is open** (default on): non-essential One UI apps are reversibly suspended, with an “unfreeze now” button and an allowlist.

### Requirements

- Samsung Galaxy A54 (**SM-A546** / a54x, Exynos 1380)
- **One UI 8**, **Android 16**
- Working **root** (KernelSU, Magisk or APatch; temporary root is possible but risky)
- The OxygenOS 16 guest image is provided by OneOS (package sets are downloaded at runtime)

### Install

1. **Uninstall official OneOS first.** This fork is signed with a different key and will not install over it. You *can* update over a previous **OneOS A54** build (same key).
2. Download `OneOS-A54-0.5.23-tr.apk` from [Releases](https://github.com/clarksonjeremy909/OneOS-A54/releases). The SHA-256 digest is in the release notes.
3. Install the APK, grant root, and prepare OxygenOS 16 from inside OneOS A54.
4. If the in-app official OneOS **Update** banner appears, **do not tap it**. The official package will not install over this signature.

### Risks and disclaimer

- Root may affect Knox, Samsung Pay and related features, and can change your warranty situation.
- OneOS is experimental and uses root on core system parts. Back up first, remove the screen lock before switching if you can, and only use a phone you can afford to factory-reset. See the warnings in the [upstream project](https://github.com/fahadalijaved/OneOS).
- This A54 build has not been fully tested on a physical phone. The first session may run warmer and slower for 10–20 minutes. Folder share is live: deleting a file in the guest can delete it on One UI too.
- Smoother performance is expected, not guaranteed. Use this software at your own risk. Not affiliated with or supported by Samsung, Google, OnePlus, or the original OneOS author.

### Developers

- **Miraç Aytaç** ([@clarksonjeremy909](https://github.com/clarksonjeremy909)) — OneOS A54 mod developer
- **Grok bot 2 (miraç)** — AI assistant
- **[fahadalijaved](https://github.com/fahadalijaved)** — original [OneOS](https://github.com/fahadalijaved/OneOS) developer

---

## License and credits

- [LICENSE](LICENSE) — Apache License 2.0 (same as upstream OneOS)
- [NOTICE](NOTICE) — required attribution, including this unofficial fork
- [CREDITS.md](CREDITS.md) — OneLineage, MysticGSI, OneUIProject, and other third-party work
- [ONEOS_A54_CHANGES.md](ONEOS_A54_CHANGES.md) — what this fork changes
- Upstream: [fahadalijaved/OneOS](https://github.com/fahadalijaved/OneOS)

Galaxy, One UI and Samsung are trademarks of Samsung Electronics; Android of Google LLC; OxygenOS of OnePlus. This project is not affiliated with any of them.
