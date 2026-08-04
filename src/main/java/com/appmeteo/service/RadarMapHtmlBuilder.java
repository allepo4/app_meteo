package com.appmeteo.service;

import com.appmeteo.model.City;
import java.util.Locale;

public class RadarMapHtmlBuilder {

    public static String build(City city) {
        return String.format(Locale.US, """
            <!DOCTYPE html>
            <html>
            <head>
              <link rel="stylesheet" href="https://unpkg.com/leaflet/dist/leaflet.css"/>
              <style>
                html, body { margin: 0; padding: 0; height: 100%%; width: 100%%; font-family: -apple-system, 'Segoe UI', Roboto, sans-serif; }
                #map { height: 100%%; width: 100%%; background: #1e2b4a; }

                #control-panel {
                  position: absolute;
                  bottom: 14px;
                  left: 50%%;
                  transform: translateX(-50%%);
                  z-index: 1000;
                  background: rgba(30, 43, 74, 0.7);
                  backdrop-filter: blur(14px);
                  -webkit-backdrop-filter: blur(14px);
                  color: #f2f2f7;
                  padding: 8px 12px;
                  border-radius: 16px;
                  display: flex;
                  flex-direction: column;
                  align-items: center;
                  gap: 6px;
                  box-shadow: 0 6px 18px rgba(0,0,0,0.35);
                  width: 82%%;
                  max-width: 320px;
                }

                #radar-time {
                  font-size: 11px;
                  font-weight: 600;
                  display: flex;
                  align-items: center;
                  gap: 6px;
                }

                .badge {
                  padding: 2px 8px;
                  border-radius: 20px;
                  font-size: 9px;
                  text-transform: uppercase;
                  font-weight: 700;
                  letter-spacing: 0.3px;
                }

                .controls-row {
                  display: flex;
                  align-items: center;
                  gap: 8px;
                  width: 100%%;
                }

                button {
                  background: rgba(255,255,255,0.15);
                  color: #f2f2f7;
                  border: none;
                  width: 24px;
                  height: 24px;
                  border-radius: 50%%;
                  cursor: pointer;
                  display: flex;
                  align-items: center;
                  justify-content: center;
                  flex-shrink: 0;
                  transition: background 0.15s ease;
                  padding: 0;
                }
                button:hover { background: rgba(255,255,255,0.28); }
                button svg { width: 10px; height: 10px; }

                input[type=range] {
                  flex-grow: 1;
                  accent-color: #64d2ff;
                  cursor: pointer;
                  height: 3px;
                }
              </style>
            </head>
            <body>
              <div id="map"></div>

              <div id="control-panel">
                <div id="radar-time">Caricamento radar…</div>
                <div class="controls-row">
                  <button id="btn-prev" title="Indietro"></button>
                  <button id="btn-play" title="Pausa / Play"></button>
                  <button id="btn-next" title="Avanti"></button>
                  <input type="range" id="radar-slider" min="0" value="0" step="1">
                </div>
              </div>

              <script src="https://unpkg.com/leaflet/dist/leaflet.js"></script>
              <script>
                var ICON_PLAY = '<svg viewBox="0 0 24 24" fill="currentColor"><path d="M8 5v14l11-7z"/></svg>';
                var ICON_PAUSE = '<svg viewBox="0 0 24 24" fill="currentColor"><rect x="6" y="5" width="4" height="14" rx="1"/><rect x="14" y="5" width="4" height="14" rx="1"/></svg>';
                var ICON_PREV = '<svg viewBox="0 0 24 24" fill="currentColor"><path d="M6 6h2v12H6zM19 6v12l-9-6z"/></svg>';
                var ICON_NEXT = '<svg viewBox="0 0 24 24" fill="currentColor"><path d="M16 6h2v12h-2zM5 6l9 6-9 6z"/></svg>';

                document.getElementById('btn-prev').innerHTML = ICON_PREV;
                document.getElementById('btn-next').innerHTML = ICON_NEXT;
                document.getElementById('btn-play').innerHTML = ICON_PAUSE;

                var map = L.map('map', { minZoom: 3, maxZoom: 12, zoomControl: false }).setView([%f, %f], 6);

                L.tileLayer('https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png', {
                  minZoom: 3,
                  maxZoom: 18,
                  attribution: '&copy; OpenStreetMap &copy; CARTO'
                }).addTo(map);

                L.control.zoom({ position: 'topright' }).addTo(map);

                fetch('https://api.rainviewer.com/public/weather-maps.json')
                  .then(r => r.json())
                  .then(data => {
                    var past = data.radar.past || [];
                    var nowcast = data.radar.nowcast || [];
                    var frames = [...past, ...nowcast];

                    if (frames.length === 0) {
                      document.getElementById('radar-time').innerText = "Radar non disponibile";
                      return;
                    }

                    var layers = frames.map(function(frame) {
                      var layer = L.tileLayer(data.host + frame.path + '/256/{z}/{x}/{y}/2/1_1.png', {
                        opacity: 0,
                        maxNativeZoom: 7
                      });
                      layer.addTo(map);
                      return layer;
                    });

                    var slider = document.getElementById('radar-slider');
                    slider.max = frames.length - 1;

                    var now = Date.now();
                    var currentIndex = 0;
                    var isPlaying = false;
                    var intervalId = null;

                    function showFrame(index) {
                      layers[currentIndex].setOpacity(0);
                      currentIndex = (index + frames.length) %% frames.length;
                      layers[currentIndex].setOpacity(0.65);
                      slider.value = currentIndex;

                      var timestamp = frames[currentIndex].time * 1000;
                      var dateObj = new Date(timestamp);
                      var formattedTime = dateObj.toLocaleTimeString('it-IT', { hour: '2-digit', minute: '2-digit' });
                      var diffMin = Math.round((timestamp - now) / 60000);

                      var badgeText, badgeBg;
                      if (Math.abs(diffMin) <= 7) {
                        badgeText = "ORA"; badgeBg = "#30d158";
                      } else if (diffMin > 7) {
                        badgeText = "+" + diffMin + " min"; badgeBg = "#bf5af2";
                      } else {
                        badgeText = diffMin + " min"; badgeBg = "#64d2ff";
                      }

                      document.getElementById('radar-time').innerHTML =
                        formattedTime + ' <span class="badge" style="background:' + badgeBg + '">' + badgeText + '</span>';
                    }

                    function startAnimation() {
                      if (intervalId) clearInterval(intervalId);
                      intervalId = setInterval(() => showFrame(currentIndex + 1), 700);
                      document.getElementById('btn-play').innerHTML = ICON_PAUSE;
                      isPlaying = true;
                    }

                    function stopAnimation() {
                      if (intervalId) clearInterval(intervalId);
                      intervalId = null;
                      document.getElementById('btn-play').innerHTML = ICON_PLAY;
                      isPlaying = false;
                    }

                    document.getElementById('btn-play').onclick = () => isPlaying ? stopAnimation() : startAnimation();
                    document.getElementById('btn-prev').onclick = () => { stopAnimation(); showFrame(currentIndex - 1); };
                    document.getElementById('btn-next').onclick = () => { stopAnimation(); showFrame(currentIndex + 1); };
                    slider.oninput = function() { stopAnimation(); showFrame(parseInt(this.value)); };

                    showFrame(0);
                    startAnimation();
                  })
                  .catch(() => {
                    document.getElementById('radar-time').innerText = "Errore caricamento radar";
                  });
              </script>
            </body>
            </html>
            """, city.getLatitude(), city.getLongitude());
    }
}