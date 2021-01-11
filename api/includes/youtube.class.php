<?php
require_once __DIR__ . "/vendor/autoload.php";

use YouTube\YouTubeDownloader;

class youtube
{
    public $m4a_mp3          = true;
    public $hide_dash_videos = false;

    public function media_info($url)
    {
        $yt = new YouTubeDownloader();

        $data               = $yt->getDownloadLinks($url);
        $links              = $data["links"];
        $json               = $data["json"];
        $video["title"]     = $json["videoDetails"]["title"];
        $video["thumbnail"] = "https://i.ytimg.com/vi/" . $json["videoDetails"]["videoId"] . "/mqdefault.jpg";
        $video["source"]    = "youtube";
        $video["links"]     = array();
        $itags              = array(18 => array('type' => 'mp4', 'itag' => 18, 'quality' => '360p', 'mute' => false), 22 => array('type' => 'mp4', 'itag' => 22, 'quality' => '720p', 'mute' => false), 91 => array('type' => 'ts', 'itag' => 91, 'quality' => '144p', 'mute' => false), 92 => array('type' => 'ts', 'itag' => 92, 'quality' => '240p', 'mute' => false), 93 => array('type' => 'ts', 'itag' => 93, 'quality' => '360p', 'mute' => false), 94 => array('type' => 'ts', 'itag' => 94, 'quality' => '480p', 'mute' => false), 95 => array('type' => 'ts', 'itag' => 95, 'quality' => '720p', 'mute' => false), 96 => array('type' => 'ts', 'itag' => 96, 'quality' => '1080p', 'mute' => false), 133 => array('type' => 'mp4', 'itag' => 133, 'quality' => '240p', 'mute' => true), 134 => array('type' => 'mp4', 'itag' => 134, 'quality' => '360p', 'mute' => true), 135 => array('type' => 'mp4', 'itag' => 135, 'quality' => '480p', 'mute' => true), 136 => array('type' => 'mp4', 'itag' => 136, 'quality' => '720p', 'mute' => true), 137 => array('type' => 'mp4', 'itag' => 137, 'quality' => '1080p', 'mute' => true), 138 => array('type' => 'mp4', 'itag' => 138, 'quality' => '4320p', 'mute' => true), 139 => array('type' => 'm4a', 'itag' => 139, 'quality' => '48kbps', 'mute' => false), 140 => array('type' => 'm4a', 'itag' => 140, 'quality' => '128kbps', 'mute' => false), 160 => array('type' => 'mp4', 'itag' => 160, 'quality' => '144p', 'mute' => true), 242 => array('type' => 'webm', 'itag' => 242, 'quality' => '240p', 'mute' => true), 243 => array('type' => 'webm', 'itag' => 243, 'quality' => '360p', 'mute' => true), 244 => array('type' => 'webm', 'itag' => 244, 'quality' => '480p', 'mute' => true), 247 => array('type' => 'webm', 'itag' => 247, 'quality' => '720p', 'mute' => true), 248 => array('type' => 'webm', 'itag' => 248, 'quality' => '1080p', 'mute' => true), 249 => array('type' => 'webm', 'itag' => 249, 'quality' => '48kbps', 'mute' => false), 250 => array('type' => 'webm', 'itag' => 250, 'quality' => '64kbps', 'mute' => false), 251 => array('type' => 'webm', 'itag' => 251, 'quality' => '160kbps', 'mute' => false), 264 => array('type' => 'mp4', 'itag' => 264, 'quality' => '1440p', 'mute' => true), 266 => array('type' => 'mp4', 'itag' => 266, 'quality' => '2160p', 'mute' => true), 271 => array('type' => 'webm', 'itag' => 271, 'quality' => '1440p', 'mute' => true), 272 => array('type' => 'webm', 'itag' => 272, 'quality' => '4320p60', 'mute' => true), 278 => array('type' => 'webm', 'itag' => 278, 'quality' => '144p', 'mute' => true), 298 => array('type' => 'mp4', 'itag' => 298, 'quality' => '720p60', 'mute' => true), 299 => array('type' => 'mp4', 'itag' => 299, 'quality' => '1080p60', 'mute' => true), 300 => array('type' => 'mp4', 'itag' => 300, 'quality' => '720p60', 'mute' => false), 301 => array('type' => 'mp4', 'itag' => 301, 'quality' => '1080p60', 'mute' => false), 302 => array('type' => 'webm', 'itag' => 302, 'quality' => '720p60', 'mute' => true), 303 => array('type' => 'webm', 'itag' => 303, 'quality' => '1080p60', 'mute' => true), 304 => array('type' => 'mp4', 'itag' => 304, 'quality' => '1440p60', 'mute' => true), 305 => array('type' => 'mp4', 'itag' => 305, 'quality' => '2160p60', 'mute' => true), 308 => array('type' => 'webm', 'itag' => 308, 'quality' => '1440p60', 'mute' => true), 313 => array('type' => 'webm', 'itag' => 313, 'quality' => '2160p', 'mute' => true), 315 => array('type' => 'webm', 'itag' => 315, 'quality' => '2160p60', 'mute' => true), 327 => array('type' => 'm4a', 'itag' => 327, 'quality' => 'kbps', 'mute' => false), 330 => array('type' => 'webm', 'itag' => 330, 'quality' => '144p60 HDR', 'mute' => true), 331 => array('type' => 'webm', 'itag' => 331, 'quality' => '240p60 HDR', 'mute' => true), 332 => array('type' => 'webm', 'itag' => 332, 'quality' => '360p60 HDR', 'mute' => true), 333 => array('type' => 'webm', 'itag' => 333, 'quality' => '480p60 HDR', 'mute' => true), 334 => array('type' => 'webm', 'itag' => 334, 'quality' => '720p60 HDR', 'mute' => true), 335 => array('type' => 'webm', 'itag' => 335, 'quality' => '1080p60 HDR', 'mute' => true), 336 => array('type' => 'webm', 'itag' => 336, 'quality' => '1440p60 HDR', 'mute' => true), 337 => array('type' => 'webm', 'itag' => 337, 'quality' => '2160p60 HDR', 'mute' => true), 338 => array('type' => 'webm', 'itag' => 338, 'quality' => 'kbps', 'mute' => false), 386 => array('type' => 'm4a', 'itag' => 386, 'quality' => 'kbps', 'mute' => false), 387 => array('type' => 'm4a', 'itag' => 387, 'quality' => 'kbps', 'mute' => false), 394 => array('type' => 'mp4', 'itag' => 394, 'quality' => '144p', 'mute' => true), 395 => array('type' => 'mp4', 'itag' => 395, 'quality' => '240p', 'mute' => true), 396 => array('type' => 'mp4', 'itag' => 396, 'quality' => '360p', 'mute' => true), 397 => array('type' => 'mp4', 'itag' => 397, 'quality' => '480p', 'mute' => true), 398 => array('type' => 'mp4', 'itag' => 398, 'quality' => '720p60', 'mute' => true), 399 => array('type' => 'mp4', 'itag' => 399, 'quality' => '1080p60', 'mute' => true), 400 => array('type' => 'mp4', 'itag' => 400, 'quality' => '1440p60', 'mute' => true), 401 => array('type' => 'mp4', 'itag' => 401, 'quality' => '2160p60', 'mute' => true), 402 => array('type' => 'mp4', 'itag' => 402, 'quality' => '4320p60', 'mute' => true), 571 => array('type' => 'mp4', 'itag' => 571, 'quality' => '4320p60', 'mute' => true));
        foreach ($links as $link) {
            if (!empty($itags[($link["itag"] ?? "")])) {
                $file_size = $this->calculate_video_size($link["itag"], $json["videoDetails"]["lengthSeconds"]);
                if ($itags[$link["itag"]]["mute"] && $this->hide_dash_videos) {
                    $is_hidden = true;
                } else {
                    array_push($video["links"], array(
                        "url"     => $link["url"],
                        "type"    => $itags[$link["itag"]]["type"],
                        "itag"    => $link["itag"],
                        "quality" => $itags[$link["itag"]]["quality"],
                        "mute"    => $itags[$link["itag"]]["mute"],
                        "size"    => $file_size,
                    ));
                    if ($this->m4a_mp3 && $itags[$link["itag"]]["type"] == "m4a") {
                        array_push($video["links"], array(
                            "url"     => $link["url"],
                            "type"    => "mp3",
                            "itag"    => $link["itag"],
                            "quality" => $itags[$link["itag"]]["quality"],
                            "mute"    => $itags[$link["itag"]]["mute"],
                            "size"    => $file_size,
                        ));
                    }
                }
            }
        }

        return $video;
    }

    private function format_bitrate($bitrate)
    {
        if ($bitrate >= 1073741824) {
            $bitrate = number_format($bitrate / 1073741824, 2) . ' GB';
        } elseif ($bitrate >= 1048576) {
            $bitrate = number_format($bitrate / 1048576, 2) . ' MB';
        } elseif ($bitrate >= 1024) {
            $bitrate = number_format($bitrate / 1024, 2) . ' KB';
        } elseif ($bitrate > 1) {
            $bitrate = $bitrate . ' bytes';
        } elseif ($bitrate === 1) {
            $bitrate = $bitrate . ' byte';
        } else {
            $bitrate = '0 bytes';
        }
        $kb = (int) $bitrate;
        return $kb . ' kbps';
    }

    private function calculate_video_size($itag, $duration)
    {
        $reference_duration = 3221;
        $reference_sizes    = [
            "249" => 20401121,
            "250" => 27038123,
            "140" => 52127912,
            "394" => 25683927,
            "278" => 32759389,
            "160" => 18337619,
            "251" => 53123830,
            "395" => 48886254,
            "242" => 62683866,
            "133" => 41932753,
            "134" => 144272120,
            "396" => 99801940,
            "243" => 127107404,
            "18"  => 252908754,
            "244" => 246450788,
            "135" => 324295771,
            "397" => 198229761,
            "22"  => 774335049,
            "398" => 435093541,
            "247" => 528682502,
            "136" => 722450659,
            "399" => 792493924,
            "248" => 963643999,
            "137" => 1419248836,
            "400" => 2747571150,
            "271" => 3134539217,
            "313" => 6715225612,
            "401" => 5770829704,
            "299" => 792093924,
            "303" => 772493924,
            "298" => 435063541,
            "302" => 432083541,
        ];
        if (isset($reference_sizes[$itag]) == "") {
            return "";
        }
        $size = ($reference_sizes[$itag] / $reference_duration) * $duration;
        return self::format_bitrate($size);
    }
}
