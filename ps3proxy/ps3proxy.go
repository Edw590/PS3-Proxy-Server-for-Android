package ps3_proxy

//package main

import (
	"bytes"
	"io/ioutil"
	"log"
	"net"
	"net/http"
	"net/url"
	"regexp"
	"strconv"
	"strings"
	"text/template"
	"time"

	"github.com/elazarl/goproxy"
)

func ExternalIP() string {
	ifaces, err := net.Interfaces()

	if err != nil {
		return "localhost"
	}

	for _, iface := range ifaces {
		if iface.Flags&net.FlagUp == 0 {
			continue // interface down
		}

		if iface.Flags&net.FlagLoopback != 0 {
			continue // loopback interface
		}

		addrs, err := iface.Addrs()

		if err != nil {
			return "localhost"
		}

		for _, addr := range addrs {
			var ip net.IP

			switch v := addr.(type) {
			case *net.IPNet:
				ip = v.IP

			case *net.IPAddr:
				ip = v.IP
			}

			if ip == nil || ip.IsLoopback() {
				continue
			}

			ip = ip.To4()

			if ip == nil {
				continue // not an ipv4 address
			}

			return ip.String()
		}
	}

	return "localhost"
}

func check(e error) int {
	if e != nil {
		return 1
	}
	return 0
}

type Exception interface{}

func Throw(up Exception) {
	panic(up)
}

func (tcef Block) Do() {
	var panicked bool = true

	if tcef.Finally != nil {
		defer tcef.Finally()
	}
	if tcef.Else != nil {
		defer func() {
			if !panicked {
				tcef.Else()
			}
		}()
	}
	if tcef.Catch != nil {
		defer func() {
			if panicked {
				tcef.Catch(recover())
			}
		}()
	}

	tcef.Try()
	panicked = false
}

type Block struct {
	Try     func()
	Catch   func(Exception)
	Else    func()
	Finally func()
}

var country string = "NONE"

func get_pictures_urls(xml_url string, ps3_like_xml string) []string {
	resp, err1 := http.Get(xml_url)
	// handle the error if there is one
	if err1 != nil {
		panic(err1)
	}
	// do this now so it won't be forgotten
	defer resp.Body.Close()
	// reads html as a slice of bytes
	xml, err2 := ioutil.ReadAll(resp.Body)
	if err2 != nil {
		panic(err2)
		return []string{}
	}
	// show the HTML code as a string %s

	var pictures_urls []string

	if ps3_like_xml == "false" {

		var xml_list_quotation_marks []string = strings.Split(string(xml), `"`)

		for i := 0; i < len(xml_list_quotation_marks); i++ {
			if strings.Contains(xml_list_quotation_marks[i], ".jpg") || strings.Contains(xml_list_quotation_marks[i], ".png") {
				var pic_already_there = 0
				for e := 0; e < len(pictures_urls); e++ {
					if xml_list_quotation_marks[i] == pictures_urls[e] {
						pic_already_there = 1
					}
				}
				if pic_already_there == 0 {
					pictures_urls = append(pictures_urls, xml_list_quotation_marks[i])
				}
			}
		}
		return pictures_urls

	} else {

		var xml_list_mtrl []string = strings.Split(string(xml), "<mtrl")

		var xml_list_mtrl_ps_plus_right_url string

		var xml_list_mtrl_ps_plus_country []string

		var num_last_ps_plus_url int = -1
		var added_one int = 0
		for i := 0; i < len(xml_list_mtrl); i++ {

			if strings.Contains(xml_list_mtrl[i], "PS Plus APRIL ") {
				if strings.Contains(xml_list_mtrl[i], "PS Plus APRIL "+country) {
					xml_list_mtrl_ps_plus_country = strings.Split(xml_list_mtrl[i], "url")
					for e := 0; e < len(xml_list_mtrl_ps_plus_country); e++ {
						if strings.Contains(xml_list_mtrl_ps_plus_country[e], "http://") && (strings.Contains(xml_list_mtrl_ps_plus_country[e], ".jpg") || strings.Contains(xml_list_mtrl_ps_plus_country[e], ".png")) {
							var url_almost_ready []string = strings.Split(xml_list_mtrl_ps_plus_country[e], ">")
							xml_list_mtrl_ps_plus_right_url = url_almost_ready[1][:len(url_almost_ready[1])-2]
						}
					}
				}
				if added_one == 0 {
					num_last_ps_plus_url += 1
					added_one = 1
				}
				num_last_ps_plus_url += 1
			}

		}
		if added_one == 1 {
			pictures_urls = append(pictures_urls, xml_list_mtrl_ps_plus_right_url)
		}

		for i := num_last_ps_plus_url + 1; i < len(xml_list_mtrl); i++ {
			var xml_list_mtrl_until []string = strings.Split(xml_list_mtrl[i], `until="`)
			var quotation_mark_index = -1

			Block{
				Try: func() {
					for i := 0; i < len(xml_list_mtrl_until[1]); i++ {
						if xml_list_mtrl_until[1][i] == '"' {
							quotation_mark_index = i
							break
						}
					}
				}, Catch: func(e Exception) {
				},
			}.Do()

			if quotation_mark_index != -1 {

				var date string = xml_list_mtrl_until[1][:quotation_mark_index]
				var year int = -1

				var indexes_hyphens []int
				for w := 0; w < len(date); w++ {
					if date[w] == '-' {
						indexes_hyphens = append(indexes_hyphens, w)
					}
				}

				temp, err := strconv.Atoi(date[:indexes_hyphens[0]])
				if err != nil {
					temp = 2100
				}
				year = temp

				if year >= time.Now().Year() {

					var xml_list_mtrl_urls []string = strings.Split(xml_list_mtrl[i], "url")
					for e := 0; e < len(xml_list_mtrl_urls); e++ {
						if strings.Contains(xml_list_mtrl_urls[e], "http://") && (strings.Contains(xml_list_mtrl_urls[e], ".jpg") || strings.Contains(xml_list_mtrl_urls[e], ".png")) {
							var url_almost_ready []string = strings.Split(xml_list_mtrl_urls[e], ">")
							pictures_urls = append(pictures_urls, url_almost_ready[1][:len(url_almost_ready[1])-2])
						}
					}

				}
			}
		}
	}
	return pictures_urls
}

/*func main() {
	Server(ExternalIP()+":8083|false|C:\\test.txt|true|http://teste.com --> C:\\teste.txtq \\\\// http://teste1.com/ --> C:\\teste.txt|false|https://www.youtube.com/feeds/videos.xml?channel_id=UC-YlkP3c1zKUPfyMMurARAQ|false|false|https://www.youtube.com/feeds/videos.xml?channel_id=UC04MPfu_LbiCL71rruQOpsA|false|false|https://www.youtube.com/feeds/videos.xml?channel_id=UC04MPfu_LbiCL71rruQOpsA|true|12345")
}*/

func Server(arguments_str string) string {
	var ret string = "3234"
	Block{
		Try: func() {
			ret = serverInternal(arguments_str)
		},
		Catch: func(e Exception) {
			ret = e.(string)
		},
	}.Do()

	return ret
}

func serverInternal(arguments_str string) string {
	var arguments []string = strings.Split(arguments_str, "|")

	var address = arguments[0]
	var manual_ps3_updatelist string = arguments[1]
	var path_ps3_updatelist string = arguments[2]
	var custom_ps3_updatelist string = arguments[3]
	var custom_rules_urls string
	var custom_rules_urls_list []string
	Block{
		Try: func() {
			custom_rules_urls = arguments[4]
			custom_rules_urls_list = strings.Split(custom_rules_urls, " \\\\// ")
		}, Catch: func(e Exception) {
			custom_rules_urls = ""
		},
	}.Do()
	var whats_new_redirect string = arguments[5]
	var whats_new_redirect_url string = arguments[6]
	if whats_new_redirect_url == "" {
		whats_new_redirect_url = "NONE_PPSFA"
	}
	var whats_new_redirect_url_ps3_like string = arguments[7]
	var ps_store_redirect string = arguments[8]
	var ps_store_redirect_url string = arguments[9]
	if ps_store_redirect_url == "" {
		ps_store_redirect_url = "NONE_PPSFA"
	}
	var ps_store_redirect_url_ps3_like string = arguments[10]
	var tv_video_services_redirect string = arguments[11]
	var tv_video_services_redirect_url string = arguments[12]
	if tv_video_services_redirect_url == "" {
		tv_video_services_redirect_url = "NONE_PPSFA"
	}
	var tv_video_services_redirect_url_ps3_like string = arguments[13]
	var session_id string = arguments[14]

	var log_num int = 0

	if path_ps3_updatelist == "" {
		path_ps3_updatelist = "NONE"
	}

	proxy := goproxy.NewProxyHttpServer()

	proxy.OnRequest().HandleConnectFunc(func(host string, ctx *goproxy.ProxyCtx) (*goproxy.ConnectAction, string) {
		log.Println("LN:" + strconv.Itoa(log_num) + " ; SI:" + session_id + " ; PPSFA > <p><u><b>" + time.Now().Format("2006-01-02 15:04:05") + "</b></u> - <b>[*]</b> HTTPS accessed <b>--</b> https:" + ctx.Req.URL.String() + "</p>")
		log_num += 1
		return goproxy.OkConnect, host
	})

	proxy.OnRequest().DoFunc(func(r *http.Request, ctx *goproxy.ProxyCtx) (*http.Request, *http.Response) {
		log.Println("LN:" + strconv.Itoa(log_num) + " ; SI:" + session_id + " ; PPSFA > <p><u><b>" + time.Now().Format("2006-01-02 15:04:05") + "</b></u> - <b>[*]</b> HTTP accessed <b>--</b> " + r.URL.String() + "</p>")
		log_num += 1
		return r, nil
	})

	if custom_ps3_updatelist == "true" {

		type Region struct{ Region, Dest, Code string }

		regions := map[string]Region{
			"jp": {"JP", "83", "jp"},
			"us": {"US", "84", "us"},
			"eu": {"EU", "85", "eu"},
			"kr": {"KR", "86", "kr"},
			"uk": {"UK", "87", "uk"},
			"mx": {"MX", "88", "mx"},
			"au": {"AU/NZ", "89", "au"},
			"sa": {"SouthAsia", "8A", "sa"},
			"tw": {"TW", "8B", "tw"},
			"ru": {"RU", "8C", "ru"},
			"cn": {"CN", "8D", "cn"},
			"br": {"BR", "8F", "br"},
		}

		proxy.OnRequest(goproxy.UrlMatches(regexp.MustCompile(`f..01.ps3.update.playstation.net/update/ps3/list/../ps3-updatelist.txt`))).DoFunc(
			func(r *http.Request, ctx *goproxy.ProxyCtx) (*http.Request, *http.Response) {

				region, ok := regions[r.URL.Path[17:19]]

				if ok {
					buf := &bytes.Buffer{}

					if manual_ps3_updatelist == "false" {
						listTmpl := template.Must(template.New("list").Parse("# {{.Region}}\r\nDest={{.Dest}};ImageVersion=00000000;SystemSoftwareVersion=0.00;CDN=http://d{{.Code}}01.ps3.update.playstation.net/update/ps3/image/{{.Code}}/nodata;CDN_Timeout=30;"))
						listTmpl.Execute(buf, region)
					} else {
						file, err := ioutil.ReadFile(path_ps3_updatelist)
						if err != nil {
							log.Println("LN:" + strconv.Itoa(log_num) + " ; SI:" + session_id + " ; PPSFA - <p><u><b>" + time.Now().Format("2006-01-02 15:04:05") + "</b></u> - <b>[/_\\]</b> 404 ERROR (File not found) on: PS3 update query redirected</p>")
							log_num += 1
							return r, goproxy.NewResponse(r,
								goproxy.ContentTypeHtml, http.StatusNotFound,
								"<center><font size='6'>-----PS3 Proxy Server for Android-----</font><p><font size='5'>404 ERROR - File not found</font></p><p><font size='4'>Please check the file path inserted. File path inserted -- "+path_ps3_updatelist+"</font></p></center>")
						}
						listTmpl := template.Must(template.New("list").Parse(string(file)))
						listTmpl.Execute(buf, region)
					}
					log.Println("LN:" + strconv.Itoa(log_num) + " ; SI:" + session_id + " ; PPSFA - <p><u><b>" + time.Now().Format("2006-01-02 15:04:05") + "</b></u> - <b>[+]</b> PS3 ps3-updatelist.txt file redirected</p>")
					log_num += 1
					return r, goproxy.NewResponse(r,
						goproxy.ContentTypeText, http.StatusOK,
						buf.String())
				}

				log.Println("LN:" + strconv.Itoa(log_num) + " ; SI:" + session_id + " ; PPSFA - <p><u><b>" + time.Now().Format("2006-01-02 15:04:05") + "</b></u> - <u><b>[/_\\]</b></u> WARNING - There was an error analizing your region. The PS3 update query will NOT be redirected.</p>")
				log_num += 1
				return r, goproxy.NewResponse(r,
					goproxy.ContentTypeHtml, http.StatusNotFound,
					"<center><font size='6'>-----PS3 Proxy Server for Android-----</font><p><font size='5'>WARNING - There was an error analizing your region</font></p><p><font size='4'>The PS3 update query will NOT be redirected.</font></p></center>")
			})
	}

	if custom_rules_urls != "" {
		for i := 0; i < len(custom_rules_urls_list); i++ {

			var url = strings.Split(custom_rules_urls_list[i], " --> ")[0]
			var final_url = strings.Split(custom_rules_urls_list[i], " --> ")[1]

			if url[len(url)-1] == '/' {
				url = url[:len(url)-1]
			}

			proxy.OnRequest().DoFunc(
				func(r *http.Request, ctx *goproxy.ProxyCtx) (*http.Request, *http.Response) {

					original_url := r.URL.String()
					if r.URL.String()[len(r.URL.String())-1] == '/' {
						original_url = r.URL.String()[:len(r.URL.String())-1]
					}

					if original_url == url {
						file, err := ioutil.ReadFile(final_url)
						if err != nil {
							log.Println("LN:" + strconv.Itoa(log_num) + " ; SI:" + session_id + " ; PPSFA - <p><u><b>" + time.Now().Format("2006-01-02 15:04:05") + "</b></u> - <b>[/_\\]</b> 404 ERROR (File not found) on: Custom URLs <b>--</b> <b>Original URL:</b> " + url + " || <b>Redirected to:</b> " + final_url + "</p>")
							log_num += 1
							return r, goproxy.NewResponse(r,
								goproxy.ContentTypeHtml, http.StatusNotFound,
								"<center><font size='6'>-----PS3 Proxy Server for Android-----</font><p><font size='5'>404 ERROR - File not found</font></p><p><font size='4'>Please check the file path inserted. File path inserted -- "+final_url+"</font></p></center>")
						}

						log.Println("LN:" + strconv.Itoa(log_num) + " ; SI:" + session_id + " ; PPSFA - <p><u><b>" + time.Now().Format("2006-01-02 15:04:05") + "</b></u> - <b>[-]</b> Custom URLs <b>--</b> <b>Original URL:</b> " + url + " || <b>Redirected to:</b> " + final_url + "</p>")
						log_num += 1
						return r, goproxy.NewResponse(r,
							"application.octet-stream", http.StatusOK,
							//Doesn't work with downloading PKGs. The PS3 wants application.octet-stream and it's getting text/html.
							//`<center><font size='6'>-----PS3 Proxy Server for Android-----</font><p><font size='5'>Now downloading the selected file...</font></p><p><font size='4'>File path inserted -- `+final_url+`</font></p></center><script>window.location.href = "http://ppsfa.download.file/";</script>`)
							string(file))
					} else {
						return r, nil
					}
				})
		}
	}

	proxy.OnRequest().DoFunc(func(r *http.Request, ctx *goproxy.ProxyCtx) (*http.Request, *http.Response) {
		/*if strings.Contains(r.URL.String(), "rss.xml") && strings.Contains(r.URL.String(), "http://infoboard.ww.dl.playstation.net/download/infoboard/p/e/") {
			var final_url string = "http://fetchrss.com/rss/5d0cf1f18a93f8e1018b45675d127c408a93f8eb5d8b4567.xml"

			var original_url string = r.URL.String()

			final_url_ready, err := url.Parse(final_url)
		    if err != nil {
		        return r, nil
		    }
			r.Host=strings.Split(final_url,"/")[2]
		    r.URL=final_url_ready

			log.Println("LN:"+strconv.Itoa(log_num)+" ; SI:"+session_id+" ; PPSFA - <p><u><b>"+time.Now().Format("2006-01-02 15:04:05")+"</b></u> - <b>[=]</b> Information Board XML redirected <b>--</b> <b>Original URL:</b> "+original_url+" || <b>Redirected to:</b> "+final_url+"</p>")
		    return r, nil
		}*/

		if (strings.Contains(r.URL.String(), ".jpg") || strings.Contains(r.URL.String(), ".png")) && strings.Contains(r.URL.String(), "http://nsx.np.dl.playstation.net/nsx/material/") {

			country = strings.ToUpper(r.URL.String()[len(r.URL.String())-2:])

			var whats_new_pictures_urls []string = get_pictures_urls("https://nsx.sec.np.dl.playstation.net/nsx/sec/Xz78TMQ1Uf31VCYr/c/NSXWSV/NSXWSV-PN.P3."+country+"-WHATSNEW00000001.xml", "true")
			var ps_store_pictures_urls []string = get_pictures_urls("https://nsx.sec.np.dl.playstation.net/nsx/sec/Xz78TMQ1Uf31VCYr/c/NSXWSV/NSXWSV-PN.P3.GAME."+country+"-BILLBOARD0000001.xml", "true")
			var ps_store_equal_whats_new int = 0

			///////////////////////////////////////////
			//If both are enabled, both places need to have the pictures synchronized because I couldn't find a way to understand which place is being requested. The target URL will be
			// What's New chosen URL and all What's New icons will be loaded. PS Store's first icon will NOT be loaded, since it's not part of What's New's pictures
			// (they need to be synchronized, in this case, both with What's New's pictures, which is the one with more pictures to be loaded, most of the time, so more news to read).
			//I tried seeing if there was an unique What's New URL being requested (which would mean it's What's New the one requested), but the PS3 loads the pictures randomly, so it didn't
			// work (it had to be in order with the first PS Store picture, which is unique in some countries, and I think in the others, What's New doesn't even work anymore).
			//Though, this would work anyways, thinking about it. The PS3 stores the pictures, so it doesn't load them again if they're necessary. So it wouldn't work. It's best this way, in my opinion.

			if ps_store_redirect == "true" && whats_new_redirect == "true" {
				var ps_store_unique_pictures []string

				var equal int
				for i := 0; i < len(ps_store_pictures_urls); i++ {
					equal = 0
					for e := 0; e < len(whats_new_pictures_urls); e++ {
						if ps_store_pictures_urls[i] == whats_new_pictures_urls[e] {
							ps_store_equal_whats_new = 1
							equal = 1
							break
						}
					}
					if equal == 0 {
						ps_store_unique_pictures = append(ps_store_unique_pictures, ps_store_pictures_urls[i])
					}
				}

				if ps_store_equal_whats_new == 1 {

					for i := 0; i < len(ps_store_unique_pictures); i++ {
						if strings.Contains(r.URL.String(), ps_store_unique_pictures[i]) {
							final_url_ready, err := url.Parse("NONE_PPSFA")
							if err != nil {
								return r, nil
							}
							r.URL = final_url_ready
							r.Host = "NONE_PPSFA"
							return r, nil
						}
					}
				}
			}

			///////////////////////////////////////////

			var picture_num int = -1

			var type_redirect string

			if tv_video_services_redirect == "true" {
				var tv_video_pictures_urls []string = get_pictures_urls("https://nsx.sec.np.dl.playstation.net/nsx/sec/Xz78TMQ1Uf31VCYr/c/NSXWSV/NSXWSV-PN.P3."+country+"-XMB_TV_INSTALL01.xml", "true")
				for i := 0; i < len(tv_video_pictures_urls); i++ {
					if strings.Split(r.URL.String(), "?")[0] == tv_video_pictures_urls[i] {
						picture_num = i
						type_redirect = "TV/Video Services"
						break
					}
				}
			}
			if ps_store_redirect == "true" && (whats_new_redirect == "false" || ps_store_equal_whats_new == 0) {
				for i := 0; i < len(ps_store_pictures_urls); i++ {
					if strings.Split(r.URL.String(), "?")[0] == ps_store_pictures_urls[i] {
						picture_num = i
						type_redirect = "PS Store"
						break
					}
				}
			}
			if whats_new_redirect == "true" {
				for i := 0; i < len(whats_new_pictures_urls); i++ {
					if strings.Split(r.URL.String(), "?")[0] == whats_new_pictures_urls[i] {
						picture_num = i
						if ps_store_redirect == "true" {
							type_redirect = "What's New(/PS Store)"
						} else {
							type_redirect = "What's New"
						}
						break
					}
				}
			}

			if picture_num == -1 {
				return r, nil
			}

			var final_url string
			var pic_not_found int = 0

			Block{
				Try: func() {
					if type_redirect == "What's New" || type_redirect == "What's New(/PS Store)" {
						final_url = get_pictures_urls(whats_new_redirect_url, whats_new_redirect_url_ps3_like)[picture_num]
					} else if type_redirect == "PS Store" {
						final_url = get_pictures_urls(ps_store_redirect_url, ps_store_redirect_url_ps3_like)[picture_num]
					} else if type_redirect == "TV/Video Services" {
						final_url = get_pictures_urls(tv_video_services_redirect_url, tv_video_services_redirect_url_ps3_like)[picture_num]
					}
				}, Catch: func(e Exception) {
					pic_not_found = 1
				},
			}.Do()

			original_url := r.URL.String()

			if pic_not_found == 1 {
				final_url_ready, err := url.Parse("NONE_PPSFA")
				if err != nil {
					return r, nil
				}
				r.Host = "NONE_PPSFA"
				r.URL = final_url_ready
			} else {
				final_url_ready, err := url.Parse(final_url)
				if err != nil {
					return r, nil
				}
				r.Host = strings.Split(final_url, "/")[2]
				r.URL = final_url_ready
			}

			log.Println("LN:" + strconv.Itoa(log_num) + " ; SI:" + session_id + " ; PPSFA - <p><u><b>" + time.Now().Format("2006-01-02 15:04:05") + "</b></u> - <b>[/]</b> " + type_redirect + " picture redirected <b>--</b> <b>Original URL:</b> " + original_url + " || <b>Redirected to:</b> " + final_url + "</p>")
			log_num += 1
			return r, nil
		} else {
			return r, nil
		} // else if r.URL.String()=="http://ppsfa.main/" {
		//	return r, goproxy.NewResponse(r,
		//				goproxy.ContentTypeHtml, http.StatusOK,
		//				`<!DOCtype html> <html> <script type="text/javascript"> function context_menu_suspension_points_click() { var context_menu_grey_rectangle1 = document.getElementById("context_menu_grey_rectangle"); var context_menu_suspension_points = document.getElementById("context_menu_suspension_points"); var context_menu_grey_rectangle2 = document.querySelector("#context_menu_grey_rectangle"); var context_menu_grey_rectangle_style = getComputedStyle(context_menu_grey_rectangle2); if (context_menu_grey_rectangle_style.display=="none") { context_menu_grey_rectangle1.style.display="block"; context_menu_suspension_points.innerHTML="-->"; } else { context_menu_grey_rectangle1.style.display="none"; context_menu_suspension_points.innerHTML="..."; } } function start_stop_button_click() { var start_stop_button1 = document.getElementById("start_stop_button"); var start_stop_button2 = document.querySelector("#start_stop_button"); var start_stop_button_style = getComputedStyle(start_stop_button2); if (start_stop_button1.text=="II") { start_stop_button1.innerHTML="&#9654;"; /*document.getElementById("start_stop_button").href="start_server.html";*/ document.getElementById("start_stop_button").href="http://ppsfa.query/stop_server.html"; } else { start_stop_button1.innerHTML="II"; /*document.getElementById("start_stop_button").href="start_server.html";*/ document.getElementById("start_stop_button").href="http://ppsfa.query/start_server.html"; } } </script> <style> .inline { float:left; } .clearBoth { clear:both; } html,body { margin:0; font-family:arial; } #everything { margin-left:8px; } #title { font-size:160%; font-weight: bold; background-color:red; width:100%; color:white; padding-top:15px; padding-bottom:15px; padding-left:15px; margin:0; position:absolute; z-index=1; } #back_arrow { font-size:25px; font-weight: bold; text-decoration:none; background-color:red; padding-top:15px; padding-bottom:15px; padding-right:20px; color:white; cursor: pointer; margin:0; } #back_arrow:hover { background-color:#ff4a4a; } #back_arrow:active { background-color:#cc0000; } #middle_space { font-size:25px; font-weight: bold; background-color:red; color:white; padding-top:9px; padding-bottom:21px; margin:0; text-align:right; } .context_menu_options { text-decoration:none; color:white; } #context_menu_suspension_points { font-size:25px; font-weight: bold; background-color:red; color:white; padding-top:10px; padding-right:25px; padding-left:25px; padding-bottom:20px; margin:0; text-align:right; cursor: pointer; right:0; text-decoration:none; z-index:2; position:absolute; } #context_menu_suspension_points:hover { background-color:#ff4a4a; } #context_menu_suspension_points:active { background-color:#cc0000; } .IP_port { padding-top:30px; padding-left:25px; margin:0; } #context_menu_grey_rectangle { background-color:#383838;color:#000000; font-size: 21px; font-family:arial; width:400px; margin:0px;margin-right:15px;margin-top:75px; padding-top:5px;padding-left:5px;padding-right:5px;padding-bottom:5px; border: 1px solid #959B9E; float:right; z-index:8; display:none; } #start_stop_button { color:black; font-size:100px; background-color:#E2DAF2; width:80px; text-decoration:none; margin:0;margin-top:50px;margin-left:90px; padding-left:20px; border: 1px solid #959B9E; cursor:pointer; position:absolute; } #start_stop_button:active { background-color:#9e9e9e } #status { font-size:40px; position:absolute; margin:0;margin-top:230px;margin-left:35px; } </style> <head> <link href="http://nsx.np.dl.playstation.net/nsx/material/b/b39f8bc7dd1ba36204382651a6d31863f885c8c1-1206980.jpg" rel="icon" type="image/x-icon"/> <title>PS3 Proxy Server for Android</title> </head> <body> <p id="title" class="inline">PS3 Proxy Server for Android</p><a id="context_menu_suspension_points" href="javascript:context_menu_suspension_points_click()">...</a> <div id="everything"> <div id="context_menu_grey_rectangle"> <a class="context_menu_options" href="http://ppsfa.instructions/" style="padding-right:73%">How to use</a> <p><a class="context_menu_options" href="http://ppsfa.settings/" style="padding-right:80%">Settings</a></p> <a class="context_menu_options" href="http://ppsfa.credits/" style="padding-right:7%">Credits, WARNING and bugs and ideas</a> </div> <p class="IP_port">IP :<input class="ip_port_input_box" type="text" placeholder="" value="localhost" style="margin-left:39px"></p> <p class="IP_port">Port :<input class="ip_port_input_box" type="number" placeholder="" value="8080" style="margin-left:25px"></p> <a href="" target="_blank" id="start_stop_button" onclick="start_stop_button_click()">&#9654;</a> <p id="status">Status: OFF</p> </div> </body> </html>`)
		//} else if r.URL.String()=="http://ppsfa.instructions/" {
		//	return r, goproxy.NewResponse(r,
		//				goproxy.ContentTypeHtml, http.StatusOK,
		//				`<!DOCtype html> <html> <script type="text/javascript"> function go_back() { window.history.back(); } </script> <style> html,body { margin:0; font-family:arial; } #everything { margin:8px; } #title { font-size:25px; font-weight: bold; background-color:red; color:white; padding-top:15px; padding-bottom:15px; margin:0; } #back_arrow { font-size:25px; font-weight: bold; text-decoration:none; background-color:red; padding-top:15px; padding-bottom:15px; padding-right:20px; color:white; cursor: pointer; margin:0; } #back_arrow:hover { background-color:#ff4a4a; } #back_arrow:active { background-color:#cc0000; } p.inline { float:left; } a.inline { float:left; } .clearBoth { clear:both; } </style> <head> <link href="http://nsx.np.dl.playstation.net/nsx/material/b/b39f8bc7dd1ba36204382651a6d31863f885c8c1-1206980.jpg" rel="icon" type="image/x-icon"/> <title>PPSFA - Instructions</title> </head> <body> <a id="back_arrow" class="inline" href="javascript:go_back()">&nbsp; <--</a><p id="title">&nbsp; &nbsp; &nbsp; PS3 Proxy Server for Android</p> <div id="everything"> <h2><font color="#07b315">Instructions</font></h2> <h3><font color="orange">On the PS3</font></h3> <p>On your PS3, select Settings ➡ Network Settings ➡ Internet Connection Settings, press ✖ to continue, select Custom method and continue until you get the Proxy Server screen. Choose Use, continue, enter the IP address you got from the app and continue until you reach the save settings page. press ✖ to save settings and press it again if you want to test the connection. You should now be able to connect to PSN. (from mondul's PS3-Proxy GitHub page, modified) <h3><font color="orange">On the app</font></h3> <p><u>Settings:</u> Go to settings and adjust them as you wish. You can choose to:</p> <p>- Start the server when the device starts;</p> <p>- Save the preferences of the server (for now, only the port, since there's nothing else to save);</p> <p>- Disable the updates notifier (not recommended, as you'll miss possible updates, unless you manually check for them from time to time);</p> <p>- Manually write ps3-updatelist.txt file, if for some reason the automatic one doesn't work for you (please tell me if it doesn't, might be a region issue - or not, and just write your own file);</p> <p>- Manually check for updates.</p> <p><u>IP:</u> For now, until someone asks for it, the IP is automatic when you start the app. If you have the server running and the IP has been changed (for example, a network change), just click Update IP and it will restart the server with the new IP you have. If the IP didn't change and you clicked Update IP, nothing will happen.</p> <p><u>Port:</u> Choose the port (or not), a number between 1024 and 65535 (if you try other numbers, it will show an error and the server won't be started).</p> <p><u>Updates notifier:</u> When there's a new update, every time you start the app, the notifier will warn you about a new update. You can choose to disable the notification for a day, clicking on Ignore on the bottom of the screen. If you click Back, the notification will not be disabled and will keep showing.</p> </div> </body> </html>`)
		//} else if strings.Contains(r.URL.String(),"http://ppsfa.query/") {
		//	return r, goproxy.NewResponse(r,
		//				goproxy.ContentTypeHtml, http.StatusOK,
		//				`<script type="text/javascript">window.close();</script>`)
		//} else {
	})

	//Then I realized I can't make the connection through the phone without changing the system settings which users might not like, as they could be doing something... But it worked on the PC (didn't get to try on the PS3)
	/*proxy.OnRequest(goproxy.UrlMatches(regexp.MustCompile("ppsfa.si."+session_id+"/shutdown.server"))).DoFunc(
	        func(r *http.Request, ctx *goproxy.ProxyCtx) (*http.Request, *http.Response) {
			    log.Println("LN:"+strconv.Itoa(log_num)+" ; SI:"+session_id+" ; PPSFA - <p><u><b>"+time.Now().Format("2006-01-02 15:04:05")+"</b></u> - <b>[!]</b> Stopped PS3 Proxy Server at " + address+"</p>")

	        	os.Exit(0)

				//This will never be shown
				return r, goproxy.NewResponse(r,
					goproxy.ContentTypeHtml, http.StatusNotFound,
	            	"<center><font size='6'>-----PS3 Proxy Server for Android-----</font><p><font size='5'>Server successefully stopped on "+time.Now().Format("2006-01-02 15:04:05")+"</font></p></center>")
	    	})*/

	log.Println("LN:" + strconv.Itoa(log_num) + " ; SI:" + session_id + " ; PPSFA - <p><u><b>" + time.Now().Format("2006-01-02 15:04:05") + "</b></u> - <b>[*]</b> Started PS3 Proxy Server at " + address + "</p>")
	log_num += 1
	http.ListenAndServe(address, proxy)

	log.Println("LN:" + strconv.Itoa(log_num) + " ; SI:" + session_id + " ; PPSFA - <p><u><b>" + time.Now().Format("2006-01-02 15:04:05") + "</b></u> - <b><u>[/_\\ ERROR /_\\]</b></u> Interrupted PS3 Proxy Server at " + address + "</p>")
	return "3234"
}
