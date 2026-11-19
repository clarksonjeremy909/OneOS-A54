# OneOS A54 (0.5.24 tabanlı özel derleme)

Paketi değiştirilmedi: `com.fahadali.oneos`. Resmi OneOS ile aynı imza olmadığından kurmadan önce resmi uygulamayı kaldırmanız gerekir.

## Arayüz
- Uygulama adı: **OneOS A54**
- Arayüz varsayılan olarak Türkçe (telefon dili İngilizce olsa bile). İngilizce metinler `values/` içinde yedek olarak durur.

## Kasma azaltma (her konuk ROM, güvenli ve geri alınabilir)
Bu ayarlar yalnızca OxygenOS için değil, hazırlanan her konuk sistem için geçerlidir. Cihaz kapıları ve SDK eşleşmesi değiştirilmedi. Tahminle paket kapatılmaz.
- İlk oturumda `oneos_dexopt.enable` açılır: sistem, uygulamaları `cmd package compile -m speed` ile derler (0.5.18’deki resmi seçenek; 0.5.22’de varsayılan kapalıydı). İlk 10–20 dakika telefon ısınabilir ve yavaş olabilir, sonra daha akıcı olması beklenir.
- “Isınınca soğut” varsayılan olarak kapalı: 45 °C’den itibaren işlemciyi kısma kaldırıldı (Ayarlar’dan yeniden açılabilir). Telefon daha çok ısınabilir; geçişte uyarı çıkabilir.
- “Her konuk sistemde kasmayı azalt” (`ONEOS_GUEST_PERF`, varsayılan açık, Ayarlar’dan kapanır):
  - animasyon ölçekleri 0.5 (window / transition / animator);
  - `get-max-cached-processes` 24’ten büyükse 24’e iner (daha düşük bir değer yükseltilmez);
  - varsa `activity_starts_logging_enabled` kapatılır;
  - I/O: gerçek disklerde (loop/ram/dm/zram değil) `read_ahead_kb` yalnızca 128’den büyükse 128 yapılır; zamanlayıcı yalnızca listede `none` varsa `none` yapılır.
  - Kullanıcı alanı ayarları konuk `/data` içinde kaydedilir; I/O değerleri aşamada saklanır ve One UI’ye dönüşte geri yüklenir. Seçenek sonraki oturumda kapatılırsa konuk ayarları da geri alınır.
- Mali/Exynos RenderEngine (SkiaGL) değiştirilmedi. Sıfır kasma garanti edilemez.

## Bring apps / veri aktarımı
- Yeni “Dosyaları aktar” bölümü: Fotoğraflar, Videolar, İndirilenler, Belgeler, Müzik/Ses (resmi klasör paylaşımı / bind; canlıdır, diğer sistemdeki değişiklik One UI dosyalarını etkileyebilir), APK’ler (One UI İndirilenler ve Belgeler’den `*.apk` dosyalarının konuk İndirilenler’e tek yönlü kopyası; buradan yüklenmez).
- Uygulamalar ve verileri mevcut “Uygulamaları getir” listesinden seçilir (tahminle yeni bir yol eklenmedi).
- One UI dosyaları asla taşınmaz veya silinmez. “Aktarımı uygula” sonraki geçiş için klasör listesini yazar. APK kopyası, sistem bir sonraki hazırlanışında / başlatılışında konuk İndirilenler klasörüne yapılır; kaynak dosyalar yalnızca okunur.


## Konuk açıkken One UI uygulamalarını dondur
- Ayarlar’da varsayılan olarak açık. Konuk oturumu başlarken OneOS, One UI’deki gerekli olmayan üçüncü taraf uygulamaları `pm suspend --user 0` ile geri alınabilir biçimde dondurur (`pm disable` / kaldırma kullanılmaz).
- Dondurulan paketlerin tam listesi aşamada (`oneos_host_suspended.list`) ve uygulamada saklanır. Konuk kapanınca (`return_to_oneui.sh` ve kök sonlandırma fiilleri) ve bir sonraki açılışta liste duruyorsa (çökme kurtarma) yalnızca o paketlere `pm unsuspend` uygulanır. “Dondurulanları şimdi geri aç” düğmesi aynı işlemi şimdi yapar.
- Sistem / privileged uygulamalar, OneOS’un kendisi, kök yöneticileri (KernelSU / Magisk / APatch ve bilinen çatallar), varsayılan başlatıcı, arama, SMS, klavye / IME, erişilebilirlik, cihaz yöneticisi, VPN, öndeki uygulama, `com.samsung.*`, `com.sec.*`, `com.android.*`, `com.google.android.gms` / `gsf` ve sizin “çalışır halde bırak” listeniz asla dokunulmaz.
- Telefon üzerinde sizin test etmeniz gerekir; dondurma geri alınabilir ama bazı uygulamalar beklediğiniz gibi uyanmayabilir.


## Upstream 0.5.23
- Resmi [v0.5.23](https://github.com/fahadalijaved/OneOS/releases/tag/v0.5.23) (`73067fd`) alındı: One UI 9.5 çağrı/IMS çökme döngüsü ve Privacy Display kapatması, One UI 9.5 kart başlığı, `oneui95` r2 seti, sürüm 166 / 0.5.23, `os_oneui.webp` ve ilgili yamalar.
- Bu sürümde yeni Android string kaynağı yok; Türkçe çeviri eklenmedi.
- A54 değişiklikleri (Türkçe arayüz, OneOS A54 adı, dexopt, ısı freni varsayılan kapalı, Dosyaları aktar, host-suspend) korundu. `guest_prepare.sh` içinde 0.5.23 IMS kontrolü ile A54 dexopt/APK kopyası birlikte duruyor (çakışma yok, farklı yerler).
- `versionName` 0.5.23, `versionCode` 166; s25 derlemesinde resmi `-s25` soneki duruyor.

## 0.5.23.1 (güncelleme kanalı ve İngilizce APK)
- `versionCode` 167, `versionName` 0.5.23.1 (s25 derlemesinde görünen ad `0.5.23.1-s25`).
- Güncelleme adresi resmi OneOS `update.json` değil, bu çatalın `version.json` dosyasıdır.
- Türkçe (`-Poneos.ui=tr`, arayüz zorlanır) ve İngilizce (`-Poneos.ui=en`, telefon diline göre `values-tr`) APK’lar aynı paket adını kullanır.
- Kasma azaltma (dexopt, ısı freni seçeneği, host-suspend, yeni `ONEOS_GUEST_PERF` ayarları) her konuk ROM için geçerlidir, yalnızca OxygenOS için değil.
- Cihaz kapıları değişmedi. README, yapımın Galaxy A54 5G için olduğunu ve başka telefonlarda garanti edilmediğini belirtir.

## Upstream 0.5.24 ve 0.5.24.1
- Resmi [v0.5.24](https://github.com/fahadalijaved/OneOS/releases/tag/v0.5.24) (`70aa91d`) alındı: raporlar artık yükleme hizmetine değil, özel OneOSReports Telegram grubuna gider (eski worker kapatıldı); parmak izi One UI'de kurulur.
- `versionCode` **168**, `versionName` **0.5.24.1** (s25 derlemesinde görünen ad `0.5.24.1-s25`). Resmi 0.5.24 de `versionCode` 167 olduğu için 0.5.23.1 ile aynı sayıdaydı; 168 her ikisinin de üstündedir.
- Güncelleme yalnızca bu çatalın `version.json` dosyasındandır. A54 kanalı resmi OneOS APK adresini kabul etmez. 0.5.23 (166) yapımlarında görülen resmi “0.5.24 kullanılabilir” bandı, resmi `update.json` dosyasındandı; 0.5.23.1 ve sonrası o dosyayı indirmez.
- Türkçe gönderim metinleri 0.5.24’teki Telegram akışına göre güncellendi.
- Önceki A54 değişiklikleri (TR/EN APK, dexopt, ısı freni, host-suspend, her konukta kasma azaltma, A54 5G README notu) korundu.


## Derleme
- Seri: s25, `packSets` kapalı (set dosyaları çalışma anında indirilir).
- İmza: kendi hata ayıklama / self anahtarı (resmi OneOS anahtarı değil).
