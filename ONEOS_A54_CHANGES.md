# OneOS A54 (0.5.23 tabanlı özel derleme)

Paketi değiştirilmedi: `com.fahadali.oneos`. Resmi OneOS ile aynı imza olmadığından kurmadan önce resmi uygulamayı kaldırmanız gerekir.

## Arayüz
- Uygulama adı: **OneOS A54**
- Arayüz varsayılan olarak Türkçe (telefon dili İngilizce olsa bile). İngilizce metinler `values/` içinde yedek olarak durur.

## Kasma azaltma (kodda var olan, güvenli seçenekler)
- İlk oturumda `oneos_dexopt.enable` açılır: sistem, uygulamaları `cmd package compile -m speed` ile derler (0.5.18’deki resmi seçenek; 0.5.22’de varsayılan kapalıydı). İlk 10–20 dakika telefon ısınabilir ve yavaş olabilir, sonra daha akıcı olması beklenir.
- “Isınınca soğut” varsayılan olarak kapalı: 45 °C’den itibaren işlemciyi kısma kaldırıldı (Ayarlar’dan yeniden açılabilir). Telefon daha çok ısınabilir; geçişte uyarı çıkabilir.
- Mali/Exynos RenderEngine (SkiaGL) değiştirilmedi. Cihaz kapıları ve SDK eşleşmesi değiştirilmedi. Sıfır kasma garanti edilemez.

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

## Derleme
- Seri: s25, `packSets` kapalı (set dosyaları çalışma anında indirilir).
- İmza: kendi hata ayıklama / self anahtarı (resmi OneOS anahtarı değil).
