!function(){function t(t){k.set(t)}function e(t){if(100!=t.get(We)&&y(ft(t,Ve))%1e4>=100*gt(t,We))throw"abort"}function n(t){if(B(ft(t,Ge)))throw"abort"}function i(){var t=q.location.protocol;if("http:"!=t&&"https:"!=t)throw"abort"}function a(e){try{H.navigator.sendBeacon?t(42):H.XMLHttpRequest&&"withCredentials"in new H.XMLHttpRequest&&t(40)}catch(t){}e.set(me,x(e),!0),e.set(Et,gt(e,Et)+1);var n=[];ht.map(function(t,i){if(i.F){var a=e.get(t);void 0!=a&&a!=i.defaultValue&&("boolean"==typeof a&&(a*=1),n.push(i.F+"="+E(""+a)))}}),n.push("z="+ct()),e.set(At,n.join("&"),!0)}function r(t){var e=ft(t,Ze)||J()+"/collect",n=ft(t,It);if(!n&&t.get(Lt)&&(n="beacon"),n){var i=ft(t,At),a=t.get(Tt),a=a||I;"image"==n?et(e,i,a):"xhr"==n&&nt(e,i,a)||"beacon"==n&&it(e,i,a)||tt(e,i,a)}else tt(e,ft(t,At),t.get(Tt));e=t.get(Ge),e=rt(e),n=e.hitcount,e.hitcount=n?n+1:1,e=t.get(Ge),delete rt(e).pending_experiments,t.set(Tt,I,!0)}function o(t){(H.gaData=H.gaData||{}).expId&&t.set(ce,(H.gaData=H.gaData||{}).expId),(H.gaData=H.gaData||{}).expVar&&t.set(ue,(H.gaData=H.gaData||{}).expVar);var e,n=t.get(Ge);if(n=rt(n).pending_experiments){var i=[];for(e in n)n.hasOwnProperty(e)&&n[e]&&i.push(encodeURIComponent(e)+"."+encodeURIComponent(n[e]));e=i.join("!")}else e=void 0;e&&t.set(he,e,!0)}function s(){if(H.navigator&&"preview"==H.navigator.loadPurpose)throw"abort"}function c(t){var e=H.gaDevIds;O(e)&&0!=e.length&&t.set("&did",e.join(","),!0)}function u(t){if(!t.get(Ge))throw"abort"}function h(e){var n=gt(e,de);500<=n&&t(15);var i=ft(e,Ct);if("transaction"!=i&&"item"!=i){var i=gt(e,ve),a=(new Date).getTime(),r=gt(e,pe);if(0==r&&e.set(pe,a),r=Math.round(2*(a-r)/1e3),0<r&&(i=Math.min(i+r,20),e.set(pe,a)),0>=i)throw"abort";e.set(ve,--i)}e.set(de,++n)}function l(e,n,i,a){n[e]=function(){try{return a&&t(a),i.apply(this,arguments)}catch(t){throw at("exc",e,t&&t.name),t}}}function f(){var t,e,n;if((n=(n=H.navigator)?n.plugins:null)&&n.length)for(var i=0;i<n.length&&!e;i++){var a=n[i];-1<a.name.indexOf("Shockwave Flash")&&(e=a.description)}if(!e)try{t=new ActiveXObject("ShockwaveFlash.ShockwaveFlash.7"),e=t.GetVariable("$version")}catch(t){}if(!e)try{t=new ActiveXObject("ShockwaveFlash.ShockwaveFlash.6"),e="WIN 6,0,21,0",t.AllowScriptAccess="always",e=t.GetVariable("$version")}catch(t){}if(!e)try{t=new ActiveXObject("ShockwaveFlash.ShockwaveFlash"),e=t.GetVariable("$version")}catch(t){}return e&&(t=e.match(/[\d]+/g))&&3<=t.length&&(e=t[0]+"."+t[1]+" r"+t[2]),e||void 0}function g(t,e,n){"none"==e&&(e="");var i=[],a=X(t);t="__utma"==t?6:2;for(var r=0;r<a.length;r++){var o=(""+a[r]).split(".");o.length>=t&&i.push({hash:o[0],R:a[r],O:o})}if(0!=i.length)return 1==i.length?i[0]:d(e,i)||d(n,i)||d(null,i)||i[0]}function d(t,e){var n,i;null==t?n=i=1:(n=y(t),i=y(T(t,".")?t.substring(1):"."+t));for(var a=0;a<e.length;a++)if(e[a].hash==n||e[a].hash==i)return e[a]}function p(t){t=t.get(Ve);var e=v(t,0);return"_ga=1."+E(e+"."+t)}function v(t,e){for(var n=new Date,i=H.navigator,a=i.plugins||[],n=[t,i.userAgent,n.getTimezoneOffset(),n.getYear(),n.getDate(),n.getHours(),n.getMinutes()+e],i=0;i<a.length;++i)n.push(a[i].description);return y(n.join("."))}function m(t,e){if(e==q.location.hostname)return!1;for(var n=0;n<t.length;n++)if(t[n]instanceof RegExp){if(t[n].test(e))return!0}else if(0<=e.indexOf(t[n]))return!0;return!1}function w(t){return 0<=t.indexOf(".")||0<=t.indexOf(":")}function y(t){var e,n,i=1;if(t)for(i=0,n=t.length-1;0<=n;n--)e=t.charCodeAt(n),i=(i<<6&268435455)+e+(e<<14),e=266338304&i,i=0!=e?i^e>>21:i;return i}var b=function(t){this.w=t||[]};b.prototype.set=function(t){this.w[t]=!0},b.prototype.encode=function(){for(var t=[],e=0;e<this.w.length;e++)this.w[e]&&(t[Math.floor(e/6)]^=1<<e%6);for(e=0;e<t.length;e++)t[e]="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_".charAt(t[e]||0);return t.join("")+"~"};var k=new b,_=function(t,e){var n=new b(j(t));n.set(e),t.set(we,n.w)},x=function(t){t=j(t),t=new b(t);for(var e=k.w.slice(),n=0;n<t.w.length;n++)e[n]=e[n]||t.w[n];return new b(e).encode()},j=function(t){return t=t.get(we),O(t)||(t=[]),t},S=function(t){return"function"==typeof t},O=function(t){return"[object Array]"==Object.prototype.toString.call(Object(t))},C=function(t){return void 0!=t&&-1<(t.constructor+"").indexOf("String")},T=function(t,e){return 0==t.indexOf(e)},A=function(t){return t?t.replace(/^[\s\xa0]+|[\s\xa0]+$/g,""):""},L=function(t){var e=q.createElement("img");return e.width=1,e.height=1,e.src=t,e},I=function(){},E=function(e){return encodeURIComponent instanceof Function?encodeURIComponent(e):(t(28),e)},D=function(e,n,i,a){try{e.addEventListener?e.addEventListener(n,i,!!a):e.attachEvent&&e.attachEvent("on"+n,i)}catch(e){t(27)}},P=/^[\w\-:\/.?=&%!]+$/,V=function(t,e,n){t&&(n?(n="",e&&P.test(e)&&(n=' id="'+e+'"'),P.test(t)&&q.write("<script"+n+' src="'+t+'"><\/script>')):(n=q.createElement("script"),n.type="text/javascript",n.async=!0,n.src=t,e&&(n.id=e),t=q.getElementsByTagName("script")[0],t.parentNode.insertBefore(n,t)))},M=function(){return"https:"==q.location.protocol},N=function(t,e){var n=t.match("(?:&|#|\\?)"+E(e).replace(/([.*+?^=!:${}()|\[\]\/\\])/g,"\\$1")+"=([^&#]*)");return n&&2==n.length?n[1]:""},G=function(){var t=""+q.location.hostname;return 0==t.indexOf("www.")?t.substring(4):t},R=function(t){var e=q.referrer;if(/^https?:\/\//i.test(e)){if(t)return e;t="//"+q.location.hostname;var n=e.indexOf(t);if(!(5!=n&&6!=n||"/"!=(t=e.charAt(n+t.length))&&"?"!=t&&""!=t&&":"!=t))return;return e}},U=function(t,e){if(1==e.length&&null!=e[0]&&"object"==typeof e[0])return e[0];for(var n={},i=Math.min(t.length+1,e.length),a=0;a<i;a++){if("object"==typeof e[a]){for(var r in e[a])e[a].hasOwnProperty(r)&&(n[r]=e[a][r]);break}a<t.length&&(n[t[a]]=e[a])}return n},F=function(){this.keys=[],this.values={},this.m={}};F.prototype.set=function(t,e,n){this.keys.push(t),n?this.m[":"+t]=e:this.values[":"+t]=e},F.prototype.get=function(t){return this.m.hasOwnProperty(":"+t)?this.m[":"+t]:this.values[":"+t]},F.prototype.map=function(t){for(var e=0;e<this.keys.length;e++){var n=this.keys[e],i=this.get(n);i&&t(n,i)}};var H=window,q=document,z=window,B=function(t){var e=z._gaUserPrefs;if(e&&e.ioo&&e.ioo()||t&&!0===z["ga-disable-"+t])return!0;try{var n=z.external;if(n&&n._gaUserPrefs&&"oo"==n._gaUserPrefs)return!0}catch(t){}return!1},X=function(t){var e=[],n=q.cookie.split(";");t=new RegExp("^\\s*"+t+"=\\s*(.*?)\\s*$");for(var i=0;i<n.length;i++){var a=n[i].match(t);a&&e.push(a[1])}return e},W=function(e,n,i,a,r,o){if(!(r=!B(r)&&!(Z.test(q.location.hostname)||"/"==i&&Y.test(a))))return!1;if(n&&1200<n.length&&(n=n.substring(0,1200),t(24)),i=e+"="+n+"; path="+i+"; ",o&&(i+="expires="+new Date((new Date).getTime()+o).toGMTString()+"; "),a&&"none"!=a&&(i+="domain="+a+";"),a=q.cookie,q.cookie=i,!(a=a!=q.cookie))t:{for(e=X(e),a=0;a<e.length;a++)if(n==e[a]){a=!0;break t}a=!1}return a},K=function(t){return E(t).replace(/\(/g,"%28").replace(/\)/g,"%29")},Y=/^(www\.)?google(\.com?)?(\.[a-z]{2})?$/,Z=/(^|\.)doubleclick\.net$/i,J=function(){return(xt||M()?"https:":"http:")+"//www.google-analytics.com"},Q=function(t){this.name="len",this.message=t+"-8192"},tt=function(t,e,n){if(n=n||I,2036>=e.length)et(t,e,n);else{if(!(8192>=e.length))throw at("len",e.length),new Q(e.length);it(t,e,n)||nt(t,e,n)||et(t,e,n)}},et=function(t,e,n){var i=L(t+"?"+e);i.onload=i.onerror=function(){i.onload=null,i.onerror=null,n()}},nt=function(t,e,n){var i=H.XMLHttpRequest;if(!i)return!1;var a=new i;return"withCredentials"in a&&(a.open("POST",t,!0),a.withCredentials=!0,a.setRequestHeader("Content-Type","text/plain"),a.onreadystatechange=function(){4==a.readyState&&(n(),a=null)},a.send(e),!0)},it=function(t,e,n){return!!H.navigator.sendBeacon&&(!!H.navigator.sendBeacon(t,e)&&(n(),!0))},at=function(t,e,n){1<=100*Math.random()||B("?")||(t=["t=error","_e="+t,"_v=j47","sr=1"],e&&t.push("_f="+e),n&&t.push("_m="+E(n.substring(0,100))),t.push("aip=1"),t.push("z="+st()),et(J()+"/collect",t.join("&"),I))},rt=function(t){var e=H.gaData=H.gaData||{};return e[t]=e[t]||{}},ot=function(){this.M=[]};ot.prototype.add=function(t){this.M.push(t)},ot.prototype.D=function(t){try{for(var e=0;e<this.M.length;e++){var n=t.get(this.M[e]);n&&S(n)&&n.call(H,t)}}catch(t){}(e=t.get(Tt))!=I&&S(e)&&(t.set(Tt,I,!0),setTimeout(e,10))};var st=function(){return Math.round(2147483647*Math.random())},ct=function(){try{var t=new Uint32Array(1);return H.crypto.getRandomValues(t),2147483647&t[0]}catch(t){return st()}},ut=function(){this.data=new F},ht=new F,lt=[];ut.prototype.get=function(t){var e=vt(t),n=this.data.get(t);return e&&void 0==n&&(n=S(e.defaultValue)?e.defaultValue():e.defaultValue),e&&e.Z?e.Z(this,t,n):n};var ft=function(t,e){var n=t.get(e);return void 0==n?"":""+n},gt=function(t,e){var n=t.get(e);return void 0==n||""===n?0:1*n};ut.prototype.set=function(t,e,n){if(t)if("object"==typeof t)for(var i in t)t.hasOwnProperty(i)&&dt(this,i,t[i],n);else dt(this,t,e,n)};var dt=function(t,e,n,i){if(void 0!=n)switch(e){case Ge:Nn.test(n)}var a=vt(e);a&&a.o?a.o(t,e,n,i):t.data.set(e,n,i)},pt=function(t,e,n,i,a){this.name=t,this.F=e,this.Z=i,this.o=a,this.defaultValue=n},vt=function(t){var e=ht.get(t);if(!e)for(var n=0;n<lt.length;n++){var i=lt[n],a=i[0].exec(t);if(a){e=i[1](a),ht.set(e.name,e);break}}return e},mt=function(t){var e;return ht.map(function(n,i){i.F==t&&(e=i)}),e&&e.name},wt=function(t,e,n,i,a){return t=new pt(t,e,n,i,a),ht.set(t.name,t),t.name},yt=function(t,e){lt.push([new RegExp("^"+t+"$"),e])},bt=function(t,e,n){return wt(t,e,n,void 0,kt)},kt=function(){},_t=C(window.GoogleAnalyticsObject)&&A(window.GoogleAnalyticsObject)||"ga",xt=!1,jt=bt("apiVersion","v"),St=bt("clientVersion","_v");wt("anonymizeIp","aip");var Ot=wt("adSenseId","a"),Ct=wt("hitType","t"),Tt=wt("hitCallback"),At=wt("hitPayload");wt("nonInteraction","ni"),wt("currencyCode","cu"),wt("dataSource","ds");var Lt=wt("useBeacon",void 0,!1),It=wt("transport");wt("sessionControl","sc",""),wt("sessionGroup","sg"),wt("queueTime","qt");var Et=wt("_s","_s");wt("screenName","cd");var Dt=wt("location","dl",""),Pt=wt("referrer","dr"),Vt=wt("page","dp","");wt("hostname","dh");var Mt=wt("language","ul"),Nt=wt("encoding","de");wt("title","dt",function(){return q.title||void 0}),yt("contentGroup([0-9]+)",function(t){return new pt(t[0],"cg"+t[1])});var Gt=wt("screenColors","sd"),Rt=wt("screenResolution","sr"),$t=wt("viewportSize","vp"),Ut=wt("javaEnabled","je"),Ft=wt("flashVersion","fl");wt("campaignId","ci"),wt("campaignName","cn"),wt("campaignSource","cs"),wt("campaignMedium","cm"),wt("campaignKeyword","ck"),wt("campaignContent","cc");var Ht=wt("eventCategory","ec"),qt=wt("eventAction","ea"),zt=wt("eventLabel","el"),Bt=wt("eventValue","ev"),Xt=wt("socialNetwork","sn"),Wt=wt("socialAction","sa"),Kt=wt("socialTarget","st"),Yt=wt("l1","plt"),Zt=wt("l2","pdt"),Jt=wt("l3","dns"),Qt=wt("l4","rrt"),te=wt("l5","srt"),ee=wt("l6","tcp"),ne=wt("l7","dit"),ie=wt("l8","clt"),ae=wt("timingCategory","utc"),re=wt("timingVar","utv"),oe=wt("timingLabel","utl"),se=wt("timingValue","utt");wt("appName","an"),wt("appVersion","av",""),wt("appId","aid",""),wt("appInstallerId","aiid",""),wt("exDescription","exd"),wt("exFatal","exf");var ce=wt("expId","xid"),ue=wt("expVar","xvar"),he=wt("exp","exp"),le=wt("_utma","_utma"),fe=wt("_utmz","_utmz"),ge=wt("_utmht","_utmht"),de=wt("_hc",void 0,0),pe=wt("_ti",void 0,0),ve=wt("_to",void 0,20);yt("dimension([0-9]+)",function(t){return new pt(t[0],"cd"+t[1])}),yt("metric([0-9]+)",function(t){return new pt(t[0],"cm"+t[1])}),wt("linkerParam",void 0,void 0,p,kt);var me=wt("usage","_u"),we=wt("_um");wt("forceSSL",void 0,void 0,function(){return xt},function(e,n,i){t(34),xt=!!i});var ye=wt("_j1","jid");yt("\\&(.*)",function(t){var e=new pt(t[0],t[1]),n=mt(t[0].substring(1));return n&&(e.Z=function(t){return t.get(n)},e.o=function(t,e,i,a){t.set(n,i,a)},e.F=void 0),e});var be=bt("_oot"),ke=wt("previewTask"),_e=wt("checkProtocolTask"),xe=wt("validationTask"),je=wt("checkStorageTask"),Se=wt("historyImportTask"),Oe=wt("samplerTask"),Ce=wt("_rlt"),Te=wt("buildHitTask"),Ae=wt("sendHitTask"),Le=wt("ceTask"),Ie=wt("devIdTask"),Ee=wt("timingTask"),De=wt("displayFeaturesTask"),Pe=bt("name"),Ve=bt("clientId","cid"),Me=bt("clientIdTime"),Ne=wt("userId","uid"),Ge=bt("trackingId","tid"),Re=bt("cookieName",void 0,"_ga"),$e=bt("cookieDomain"),Ue=bt("cookiePath",void 0,"/"),Fe=bt("cookieExpires",void 0,63072e3),He=bt("legacyCookieDomain"),qe=bt("legacyHistoryImport",void 0,!0),ze=bt("storage",void 0,"cookie"),Be=bt("allowLinker",void 0,!1),Xe=bt("allowAnchor",void 0,!0),We=bt("sampleRate","sf",100),Ke=bt("siteSpeedSampleRate",void 0,1),Ye=bt("alwaysSendReferrer",void 0,!1),Ze=wt("transportUrl"),Je=wt("_r","_r"),Qe=function(t){this.V=t,this.fa=void 0,this.$=!1,this.oa=void 0,this.ea=1},tn=function(t,e){var n;if(t.fa&&t.$)return 0;if(t.$=!0,e){if(t.oa&&gt(e,t.oa))return gt(e,t.oa);if(0==e.get(Ke))return 0}return 0==t.V?0:(void 0===n&&(n=ct()),0==n%t.V?Math.floor(n/t.V)%t.ea+1:0)},en=function(t){var e=Math.min(gt(t,Ke),100);return!(y(ft(t,Ve))%100>=e)},nn=function(t){var e={};if(an(e)||rn(e)){var n=e[Yt];void 0==n||1/0==n||isNaN(n)||(0<n?(on(e,Jt),on(e,ee),on(e,te),on(e,Zt),on(e,Qt),on(e,ne),on(e,ie),t(e)):D(H,"load",function(){nn(t)},!1))}},an=function(t){var e=H.performance||H.webkitPerformance,e=e&&e.timing;if(!e)return!1;var n=e.navigationStart;return 0!=n&&(t[Yt]=e.loadEventStart-n,t[Jt]=e.domainLookupEnd-e.domainLookupStart,t[ee]=e.connectEnd-e.connectStart,t[te]=e.responseStart-e.requestStart,t[Zt]=e.responseEnd-e.responseStart,t[Qt]=e.fetchStart-n,t[ne]=e.domInteractive-n,t[ie]=e.domContentLoadedEventStart-n,!0)},rn=function(t){if(H.top!=H)return!1;var e=H.external,n=e&&e.onloadT;return e&&!e.isValidLoadTime&&(n=void 0),2147483648<n&&(n=void 0),0<n&&e.setPageReadyTime(),void 0!=n&&(t[Yt]=n,!0)},on=function(t,e){var n=t[e];(isNaN(n)||1/0==n||0>n)&&(t[e]=void 0)},sn=function(t){return function(e){if("pageview"==e.get(Ct)&&!t.I){t.I=!0;var n=en(e);e=0<N(e.get(Dt),"gclid").length,(n||e)&&nn(function(e){t.send(n?"timing":"adtiming",e)})}}},cn=!1,un=function(e){if("cookie"==ft(e,ze)){var n=ft(e,Re),i=fn(e),a=pn(ft(e,Ue)),r=dn(ft(e,$e)),o=1e3*gt(e,Fe),s=ft(e,Ge);if("auto"!=r)W(n,i,a,r,s,o)&&(cn=!0);else{t(32);var c;if(i=[],r=G().split("."),4!=r.length||(c=r[r.length-1],parseInt(c,10)!=c)){for(c=r.length-2;0<=c;c--)i.push(r.slice(c).join("."));i.push("none"),c=i}else c=["none"];for(var u=0;u<c.length;u++)if(r=c[u],e.data.set($e,r),i=fn(e),W(n,i,a,r,s,o))return void(cn=!0);e.data.set($e,"auto")}}},hn=function(t){if("cookie"==ft(t,ze)&&!cn&&(un(t),!cn))throw"abort"},ln=function(e){if(e.get(qe)){var n=ft(e,$e),i=ft(e,He)||G(),a=g("__utma",i,n);a&&(t(19),e.set(ge,(new Date).getTime(),!0),e.set(le,a.R),(n=g("__utmz",i,n))&&a.hash==n.hash&&e.set(fe,n.R))}},fn=function(t){var e=K(ft(t,Ve)),n=dn(ft(t,$e)).split(".").length;return t=vn(ft(t,Ue)),1<t&&(n+="-"+t),["GA1",n,e].join(".")},gn=function(t,e,n){for(var i,a=[],r=[],o=0;o<t.length;o++){var s=t[o];s.H[n]==e?a.push(s):void 0==i||s.H[n]<i?(r=[s],i=s.H[n]):s.H[n]==i&&r.push(s)}return 0<a.length?a:r},dn=function(t){return 0==t.indexOf(".")?t.substr(1):t},pn=function(t){return t?(1<t.length&&t.lastIndexOf("/")==t.length-1&&(t=t.substr(0,t.length-1)),0!=t.indexOf("/")&&(t="/"+t),t):"/"},vn=function(t){return t=pn(t),"/"==t?1:t.split("/").length},mn=new RegExp(/^https?:\/\/([^\/:]+)/),wn=/(.*)([?&#])(?:_ga=[^&#]*)(?:&?)(.*)/,yn=function(e){t(48),this.target=e,this.T=!1};yn.prototype.ca=function(t,e){if(t.tagName){if("a"==t.tagName.toLowerCase())return void(t.href&&(t.href=bn(this,t.href,e)));if("form"==t.tagName.toLowerCase())return kn(this,t)}if("string"==typeof t)return bn(this,t,e)};var bn=function(t,e,n){var i=wn.exec(e);i&&3<=i.length&&(e=i[1]+(i[3]?i[2]+i[3]:"")),t=t.target.get("linkerParam");var a=e.indexOf("?"),i=e.indexOf("#");return n?e+=(-1==i?"#":"&")+t:(n=-1==a?"?":"&",e=-1==i?e+(n+t):e.substring(0,i)+n+t+e.substring(i)),e=e.replace(/&+_ga=/,"&_ga=")},kn=function(t,e){if(e&&e.action){var n=t.target.get("linkerParam").split("=")[1];if("get"==e.method.toLowerCase()){for(var i=e.childNodes||[],a=0;a<i.length;a++)if("_ga"==i[a].name)return void i[a].setAttribute("value",n);i=q.createElement("input"),i.setAttribute("type","hidden"),i.setAttribute("name","_ga"),i.setAttribute("value",n),e.appendChild(i)}else"post"==e.method.toLowerCase()&&(e.action=bn(t,e.action))}};yn.prototype.S=function(e,n,i){function a(i){try{i=i||H.event;var a;t:{var o=i.target||i.srcElement;for(i=100;o&&0<i;){if(o.href&&o.nodeName.match(/^a(?:rea)?$/i)){a=o;break t}o=o.parentNode,i--}a={}}("http:"==a.protocol||"https:"==a.protocol)&&m(e,a.hostname||"")&&a.href&&(a.href=bn(r,a.href,n))}catch(e){t(26)}}var r=this;this.T||(this.T=!0,D(q,"mousedown",a,!1),D(q,"keyup",a,!1)),i&&D(q,"submit",function(t){if(t=t||H.event,(t=t.target||t.srcElement)&&t.action){var n=t.action.match(mn);n&&m(e,n[1])&&kn(r,t)}})};var _n,xn=/^(GTM|OPT)-[A-Z0-9]+$/,jn=/;_gaexp=[^;]*/g,Sn=/;((__utma=)|([^;=]+=GAX?\d+\.))[^;]*/g,On=function(t){function e(t,e){e&&(n+="&"+t+"="+E(e))}var n="https://www.google-analytics.com/gtm/js?id="+E(t.id);return"dataLayer"!=t.B&&e("l",t.B),e("t",t.target),e("cid",t.ja),e("cidt",t.ka),e("gac",t.la),e("aip",t.ia),t.na&&e("m","sync"),e("cycle",t.G),n},Cn=function(e,n,i){this.U=ye,this.aa=n,(n=i)||(n=(n=ft(e,Pe))&&"t0"!=n?En.test(n)?"_gat_"+K(ft(e,Ge)):"_gat_"+K(n):"_gat"),this.Y=n,tn(new Qe(100),e)&&(t(30),this.pa=!0)},Tn=function(t,e){var n=e.get(Te);e.set(Te,function(e){An(t,e);var i=n(e);return Ln(t,e),i});var i=e.get(Ae);e.set(Ae,function(e){var n=i(e);return In(t,e),n})},An=function(t,e){e.get(t.U)||("1"==X(t.Y)[0]?e.set(t.U,"",!0):e.set(t.U,""+st(),!0))},Ln=function(t,e){if(e.get(t.U)){var n=6e5;t.pa&&(n/=10),W(t.Y,"1",e.get(Ue),e.get($e),e.get(Ge),n)}},In=function(t,e){if(e.get(t.U)){var n=new F,i=function(t){vt(t).F&&n.set(vt(t).F,e.get(t))};i(jt),i(St),i(Ge),i(Ve),i(Ne),i(t.U),n.set(vt(me).F,x(e));var a=t.aa;n.map(function(t,e){a+=E(t)+"=",a+=E(""+e)+"&"}),a+="z="+st(),L(a),e.set(t.U,"",!0)}},En=/^gtm\d+$/,Dn=function(t,e){var n=t.b;if(!n.get("dcLoaded")){_(n,29),e=e||{};var i;e[Re]&&(i=K(e[Re])),i=new Cn(n,"https://stats.g.doubleclick.net/r/collect?t=dc&aip=1&_r=3&",i),Tn(i,n),n.set("dcLoaded",!0)}},Pn=function(t){if(!t.get("dcLoaded")&&"cookie"==t.get(ze)){_(t,51);var e=new Cn(t);An(e,t),Ln(e,t),t.get(e.U)&&(t.set(Je,1,!0),t.set(Ze,J()+"/r/collect",!0))}},Vn=function(){var t=H.gaGlobal=H.gaGlobal||{};return t.hid=t.hid||st()},Mn=function(t,e,n){if(!_n){var i;i=q.location.hash;var a=H.name,r=/^#?gaso=([^&]*)/;(a=(i=(i=i&&i.match(r)||a&&a.match(r))?i[1]:X("GASO")[0]||"")&&i.match(/^(?:!([-0-9a-z.]{1,40})!)?([-.\w]{10,1200})$/i))&&(W("GASO",""+i,n,e,t,0),window._udo||(window._udo=e),window._utcp||(window._utcp=n),t=a[1],V("https://www.google.com/analytics/web/inpage/pub/inpage.js?"+(t?"prefix="+t+"&":"")+st(),"_gasojs")),_n=!0}},Nn=/^(UA|YT|MO|GP)-(\d+)-(\d+)$/,Gn=function(t){function l(t,e){g.b.data.set(t,e)}function f(t,e){l(t,e),g.filters.add(t)}var g=this;this.b=new ut,this.filters=new ot,l(Pe,t[Pe]),l(Ge,A(t[Ge])),l(Re,t[Re]),l($e,t[$e]||G()),l(Ue,t[Ue]),l(Fe,t[Fe]),l(He,t[He]),l(qe,t[qe]),l(Be,t[Be]),l(Xe,t[Xe]),l(We,t[We]),l(Ke,t[Ke]),l(Ye,t[Ye]),l(ze,t[ze]),l(Ne,t[Ne]),l(Me,t[Me]),l(jt,1),l(St,"j47"),f(be,n),f(ke,s),f(_e,i),f(xe,u),f(je,hn),f(Se,ln),f(Oe,e),f(Ce,h),f(Le,o),f(Ie,c),f(De,Pn),f(Te,a),f(Ae,r),f(Ee,sn(this)),Rn(this.b,t[Ve]),$n(this.b),this.b.set(Ot,Vn()),Mn(this.b.get(Ge),this.b.get($e),this.b.get(Ue))},Rn=function(e,n){if("cookie"==ft(e,ze)){cn=!1;var i;t:{var a=X(ft(e,Re));if(a&&!(1>a.length)){i=[];for(var r=0;r<a.length;r++){var o;o=a[r].split(".");var s=o.shift();("GA1"==s||"1"==s)&&1<o.length?(s=o.shift().split("-"),1==s.length&&(s[1]="1"),s[0]*=1,s[1]*=1,o={H:s,s:o.join(".")}):o=void 0,o&&i.push(o)}if(1==i.length){t(13),i=i[0].s;break t}if(0!=i.length){if(t(14),a=dn(ft(e,$e)).split(".").length,i=gn(i,a,0),1==i.length){i=i[0].s;break t}a=vn(ft(e,Ue)),i=gn(i,a,1),i=i[0]&&i[0].s;break t}t(12)}i=void 0}i||(i=ft(e,$e),a=ft(e,He)||G(),i=g("__utma",a,i),void 0!=i?(t(10),i=i.O[1]+"."+i.O[2]):i=void 0),i&&(e.data.set(Ve,i),cn=!0)}if(i=e.get(Xe),(r=N(q.location[i?"href":"search"],"_ga"))&&(e.get(Be)?(i=r.indexOf("."),-1==i?t(22):(a=r.substring(i+1),"1"!=r.substring(0,i)?t(22):(i=a.indexOf("."),-1==i?t(22):(r=a.substring(0,i),i=a.substring(i+1),r!=v(i,0)&&r!=v(i,-1)&&r!=v(i,-2)?t(23):(t(11),e.data.set(Ve,i)))))):t(21)),n&&(t(9),e.data.set(Ve,E(n))),!e.get(Ve))if(i=(i=H.gaGlobal&&H.gaGlobal.vid)&&-1!=i.search(/^(?:utma\.)?\d+\.\d+$/)?i:void 0)t(17),e.data.set(Ve,i);else{for(t(8),i=H.navigator.userAgent+(q.cookie?q.cookie:"")+(q.referrer?q.referrer:""),a=i.length,r=H.history.length;0<r;)i+=r--^a++;e.data.set(Ve,[st()^2147483647&y(i),Math.round((new Date).getTime()/1e3)].join("."))}un(e)},$n=function(e){var n=H.navigator,i=H.screen,a=q.location;if(e.set(Pt,R(e.get(Ye))),a){var r=a.pathname||"";"/"!=r.charAt(0)&&(t(31),r="/"+r),e.set(Dt,a.protocol+"//"+a.hostname+r+a.search)}i&&e.set(Rt,i.width+"x"+i.height),i&&e.set(Gt,i.colorDepth+"-bit");var i=q.documentElement,o=(r=q.body)&&r.clientWidth&&r.clientHeight,s=[];if(i&&i.clientWidth&&i.clientHeight&&("CSS1Compat"===q.compatMode||!o)?s=[i.clientWidth,i.clientHeight]:o&&(s=[r.clientWidth,r.clientHeight]),i=0>=s[0]||0>=s[1]?"":s.join("x"),e.set($t,i),e.set(Ft,f()),e.set(Nt,q.characterSet||q.charset),e.set(Ut,n&&"function"==typeof n.javaEnabled&&n.javaEnabled()||!1),e.set(Mt,(n&&(n.language||n.browserLanguage)||"").toLowerCase()),a&&e.get(Xe)&&(n=q.location.hash)){for(n=n.split(/[?&#]+/),a=[],i=0;i<n.length;++i)(T(n[i],"utm_id")||T(n[i],"utm_campaign")||T(n[i],"utm_source")||T(n[i],"utm_medium")||T(n[i],"utm_term")||T(n[i],"utm_content")||T(n[i],"gclid")||T(n[i],"dclid")||T(n[i],"gclsrc"))&&a.push(n[i]);0<a.length&&(n="#"+a.join("&"),e.set(Dt,e.get(Dt)+n))}};Gn.prototype.get=function(t){return this.b.get(t)},Gn.prototype.set=function(t,e){this.b.set(t,e)};var Un={pageview:[Vt],event:[Ht,qt,zt,Bt],social:[Xt,Wt,Kt],timing:[ae,re,se,oe]};Gn.prototype.send=function(t){if(!(1>arguments.length)){var e,n;"string"==typeof arguments[0]?(e=arguments[0],n=[].slice.call(arguments,1)):(e=arguments[0]&&arguments[0][Ct],n=arguments),e&&(n=U(Un[e]||[],n),n[Ct]=e,this.b.set(n,void 0,!0),this.filters.D(this.b),this.b.data.m={})}},Gn.prototype.ma=function(t,e){var n=this;Yn(t,n,e)||(Jn(t,function(){Yn(t,n,e)}),Zn(String(n.get(Pe)),t,void 0,e,!0))};var Fn,Hn,qn,zn,Bn=function(t){return"prerender"!=q.visibilityState&&(t(),!0)},Xn=function(e){if(!Bn(e)){t(16);var n=!1,i=function(){if(!n&&Bn(e)){n=!0;var t=i,a=q;a.removeEventListener?a.removeEventListener("visibilitychange",t,!1):a.detachEvent&&a.detachEvent("onvisibilitychange",t)}};D(q,"visibilitychange",i)}},Wn=/^(?:(\w+)\.)?(?:(\w+):)?(\w+)$/,Kn=function(t){if(S(t[0]))this.u=t[0];else{var e=Wn.exec(t[0]);if(null!=e&&4==e.length&&(this.c=e[1]||"t0",this.K=e[2]||"",this.C=e[3],this.a=[].slice.call(t,1),this.K||(this.A="create"==this.C,this.i="require"==this.C,this.g="provide"==this.C,this.ba="remove"==this.C),this.i&&(3<=this.a.length?(this.X=this.a[1],this.W=this.a[2]):this.a[1]&&(C(this.a[1])?this.X=this.a[1]:this.W=this.a[1]))),e=t[1],t=t[2],!this.C)throw"abort";if(this.i&&(!C(e)||""==e))throw"abort";if(this.g&&(!C(e)||""==e||!S(t)))throw"abort";if(w(this.c)||w(this.K))throw"abort";if(this.g&&"t0"!=this.c)throw"abort"}};Fn=new F,qn=new F,zn=new F,Hn={ec:45,ecommerce:46,linkid:47};var Yn=function(t,e,n){e==ii||e.get(Pe);var i=Fn.get(t);return!!S(i)&&(e.plugins_=e.plugins_||new F,!!e.plugins_.get(t)||(e.plugins_.set(t,new i(e,n||{})),!0))},Zn=function(e,n,i,a,r){if(!S(Fn.get(n))&&!qn.get(n)){if(Hn.hasOwnProperty(n)&&t(Hn[n]),xn.test(n)){if(t(52),!(e=ii.j(e)))return!0;i=a||{},a={id:n,B:i.dataLayer||"dataLayer",ia:!!e.get("anonymizeIp"),na:r,G:!1},e.get("&gtm")==n&&(a.G=!0);var o=String(e.get("name"));"t0"!=o&&(a.target=o),B(String(e.get("trackingId")))||(a.ja=String(e.get(Ve)),a.ka=Number(e.get(Me)),e=i.palindrome?Sn:jn,e=(e=q.cookie.replace(/^|(; +)/g,";").match(e))?e.sort().join("").substring(1):void 0,a.la=e),e=a.B,i=(new Date).getTime(),H[e]=H[e]||[],i={"gtm.start":i},r||(i.event="gtm.js"),H[e].push(i),i=On(a)}!i&&Hn.hasOwnProperty(n)?(t(39),i=n+".js"):t(43),i&&(i&&0<=i.indexOf("/")||(i=(xt||M()?"https:":"http:")+"//www.google-analytics.com/plugins/ua/"+i),a=ei(i),e=a.protocol,i=q.location.protocol,("https:"==e||e==i||("http:"!=e?0:"http:"==i))&&ti(a)&&(V(a.url,void 0,r),qn.set(n,!0)))}},Jn=function(t,e){var n=zn.get(t)||[];n.push(e),zn.set(t,n)},Qn=function(t,e){Fn.set(t,e);for(var n=zn.get(t)||[],i=0;i<n.length;i++)n[i]();zn.set(t,[])},ti=function(t){var e=ei(q.location.href);return!!T(t.url,"https://www.google-analytics.com/gtm/js?id=")||!(t.query||0<=t.url.indexOf("?")||0<=t.path.indexOf("://"))&&(t.host==e.host&&t.port==e.port||(e="http:"==t.protocol?80:443,!("www.google-analytics.com"!=t.host||(t.port||e)!=e||!T(t.path,"/plugins/"))))},ei=function(t){function e(t){var e=(t.hostname||"").split(":")[0].toLowerCase(),n=(t.protocol||"").toLowerCase(),n=1*t.port||("http:"==n?80:"https:"==n?443:"");return t=t.pathname||"",T(t,"/")||(t="/"+t),[e,""+n,t]}var n=q.createElement("a");n.href=q.location.href;var i=(n.protocol||"").toLowerCase(),a=e(n),r=n.search||"",o=i+"//"+a[0]+(a[1]?":"+a[1]:"");return T(t,"//")?t=i+t:T(t,"/")?t=o+t:!t||T(t,"?")?t=o+a[2]+(t||r):0>t.split("/")[0].indexOf(":")&&(t=o+a[2].substring(0,a[2].lastIndexOf("/"))+"/"+t),n.href=t,i=e(n),{protocol:(n.protocol||"").toLowerCase(),host:i[0],port:i[1],path:i[2],query:n.search||"",url:t||""}},ni={ga:function(){ni.f=[]}};ni.ga(),ni.D=function(t){var e=ni.J.apply(ni,arguments),e=ni.f.concat(e);for(ni.f=[];0<e.length&&!ni.v(e[0])&&(e.shift(),!(0<ni.f.length)););ni.f=ni.f.concat(e)},ni.J=function(t){for(var e=[],n=0;n<arguments.length;n++)try{var i=new Kn(arguments[n]);i.g?Qn(i.a[0],i.a[1]):(i.i&&(i.ha=Zn(i.c,i.a[0],i.X,i.W)),e.push(i))}catch(t){}return e},ni.v=function(t){try{if(t.u)t.u.call(H,ii.j("t0"));else{var e=t.c==_t?ii:ii.j(t.c);if(t.A)"t0"!=t.c||ii.create.apply(ii,t.a);else if(t.ba)ii.remove(t.c);else if(e)if(t.i){if(t.ha&&(t.ha=Zn(t.c,t.a[0],t.X,t.W)),!Yn(t.a[0],e,t.W))return!0}else if(t.K){var n=t.C,i=t.a,a=e.plugins_.get(t.K);a[n].apply(a,i)}else e[t.C].apply(e,t.a)}}catch(t){}};var ii=function(e){t(1),ni.D.apply(ni,[arguments])};ii.h={},ii.P=[],ii.L=0,ii.answer=42;var ai=[Ge,$e,Pe];ii.create=function(t){var e=U(ai,[].slice.call(arguments));e[Pe]||(e[Pe]="t0");var n=""+e[Pe];return ii.h[n]?ii.h[n]:(e=new Gn(e),ii.h[n]=e,ii.P.push(e),e)},ii.remove=function(t){for(var e=0;e<ii.P.length;e++)if(ii.P[e].get(Pe)==t){ii.P.splice(e,1),ii.h[t]=null;break}},ii.j=function(t){return ii.h[t]},ii.getAll=function(){return ii.P.slice(0)},ii.N=function(){"ga"!=_t&&t(49);var e=H[_t];if(!e||42!=e.answer){ii.L=e&&e.l,ii.loaded=!0;var n=H[_t]=ii;if(l("create",n,n.create),l("remove",n,n.remove),l("getByName",n,n.j,5),l("getAll",n,n.getAll,6),n=Gn.prototype,l("get",n,n.get,7),l("set",n,n.set,4),l("send",n,n.send),l("requireSync",n,n.ma),n=ut.prototype,l("get",n,n.get),l("set",n,n.set),!M()&&!xt){t:{for(var n=q.getElementsByTagName("script"),i=0;i<n.length&&100>i;i++){var a=n[i].src;if(a&&0==a.indexOf("https://www.google-analytics.com/analytics")){t(33),n=!0;break t}}n=!1}n&&(xt=!0)}M()||xt||!tn(new Qe(1e4))||(t(36),xt=!0),(H.gaplugins=H.gaplugins||{}).Linker=yn,n=yn.prototype,Qn("linker",yn),l("decorate",n,n.ca,20),l("autoLink",n,n.S,25),Qn("displayfeatures",Dn),Qn("adfeatures",Dn),e=e&&e.q,O(e)?ni.D.apply(ii,e):t(50)}},ii.da=function(){for(var t=ii.getAll(),e=0;e<t.length;e++)t[e].get(Pe)};var ri=ii.N,oi=H[_t];oi&&oi.r?ri():Xn(ri),Xn(function(){ni.D(["provide","render",I])})}(window);