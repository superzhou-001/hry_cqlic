<template>
    <div class="hello">
        <mt-header fixed>
            <mt-button slot="right" @click="myChaneLange(1)">{{langeName}}</mt-button>
        </mt-header>
        <div class="marTop">
            <div class="header-title">
                <!--<div class="title-tel">
                    <div class="title-register" :class="currentTab==='phonezh'? 'cur':''" @click="toggleTab('phonezh')">{{shoujizhuce}}</div>
                </div>-->
                <div class="title-tel">
                    <div class="register-email" :class="currentTab==='emailzh'? 'cur':''" @click="toggleTab('emailzh')">{{youxianzhuce}}</div>
                </div>
            </div>
        </div>
        <div class="myForm">

            <div class="inpbox border-bottom">
                <input type="text" class="phone" :placeholder="this.please_write_email" v-model="phoneNumber" style="font-size:11px;" @blur="onPhone()">
            </div>
            <div class="inpbox border-bottom" style="position: relative;">
                <input type="text" class="yanzhengma" :placeholder="this.imageCodeisnull" v-model="imgCode" style="font-size:11px;">
                <span class="spbtn">
                      <img :src='myimgCode' style="width:100%;height:100%;" @click="myImgCode()"/>
                </span>
            </div>
            <div class="inpbox border-bottom" style="position: relative;">
                <input type="text" class="yanzhengma" :placeholder="this.qingshuryouxyzm" v-model="registSmsCode" style="font-size:11px;">
                <span class="spbtn" @click="geCode" v-show="show" style="top:28px;">{{huoquyanzm}}</span>
                <span class="spbtn" v-show="!show" style="top:28px;">{{count}} s</span>
            </div>
            <div class="inpbox border-bottom">
                <input type="password" class="password" :placeholder="this.jiaoyitishi2" v-model="password" style="font-size:11px;">
            </div>
            <div class="inpbox border-bottom">
                <input type="password" class="password" :placeholder="this.querenmima" v-model="opassword" style="font-size:11px;">
            </div>
            <div class="inpbox border-bottom">
                <input type="text" readonly class="password" :placeholder="this.tuijianrenshoujihao" v-model="referralCode" style="font-size:11px;">
            </div>
            <div class="myAgree">
                <input type="checkbox" v-model="myAgree">
                <label>{{yuedujieshou}}</label>
                <i @click="myRegAgree()">???{{fuwuxieyi}}???</i>
            </div>
        </div>
        <div class="myRegister-btn">
            <mt-button type="primary" size="large" @click.native="myRegBtn()">{{zhuce}}</mt-button>
        </div>



        <!--??????????????????-->
        <mt-popup
            v-model="popupLange"
            position="right"
            class="hellos"
            style="width: 100%;height: 100%;">
                <mt-header fixed :title='this.szyuyin'>
                    <mt-button icon="back" slot="left" @click.native="goBack"></mt-button>
                </mt-header>
                <a class="mint-cell page-part" style="padding-top: 40px;">
                    <div class="mint-cell-left"></div>
                    <div class="mint-cell-wrapper" v-for='(item,index) in langeList' :key="index" @click="changLang(item.name, item.value)">
                        <div class="mint-cell-title" style="text-align: left;">
                            <span>
                                {{item.name}}
                            </span>
                        </div>
                        <div class="mint-cell-value" v-if='item.name == langeName'>???</div>
                    </div>
                </a>
        </mt-popup>
        <!--??????????????????-->

        <!--??????????????????-->
        <mt-popup
            v-model="popupContry"
            lockScroll="false"
            position="right"
            class="hellos"
            style="width: 100%;height: 100%;">
                <mt-header fixed :title='this.szyuyin'>
                    <mt-button icon="back" slot="left" @click.native="goBack"></mt-button>
                </mt-header>
                <a class="mint-cell page-part" style="padding-top: 40px;overflow-y: auto;height: 100%;">
                    <div class="mint-cell-left"></div>
                    <div class="mint-cell-wrapper" v-for="(item,index) in countryicon" :key="index" @click="select(item)">
                        <div class="mint-cell-title" style="text-align: left;">
                            <span>
                                {{item.content}}&nbsp;&nbsp;&nbsp;{{item.countryNum}}
                            </span>
                        </div>
                    </div>
                </a>
        </mt-popup>
        <!--??????????????????-->

        <!--??????????????????-->
        <mt-popup
            v-model="popupVisible"
            position="right"
            class="hellos"
            style="width: 100%;height: 100%;">
                <mt-header fixed :title='this.fuwuxieyi'>
                    <mt-button icon="back" slot="left" @click.native="goBack"></mt-button>
                </mt-header>
                <div style="padding: 50px 10px 10px 10px;" v-html="regreg"></div>
        </mt-popup>
        <!--??????????????????-->
    </div>
</template>

<script>
import { MessageBox } from 'mint-ui';
import { Toast } from 'mint-ui';
export default {
    name: '',
    data () {
        return {
            extensionCode:'',
            referralCode:'',
            registSmsCode:'',
            regreg:'',
            zhuce:'',
            szyuyin:'',
            shoujizhuce:'',
            youxianzhuce:'',
            shurshoujhm:'',
            please_write_email: '',
            shoujiguishu:'',
            emailisnull:'',
            emailvail:'',
            zhengquesjhm:'',
            tishi:'',
            sure:'',
            please_write_email:'',
            duanxinyanzma:'',
            newpwd_no_null:'',
            passvail:'',
            twopwd_is_diff:'',
            imageCodeisnull:'',
            jiaoyitishi2:'',
            querenmima:'',
            tuijianrenshoujihao:'',
            fuwuxieyi:'',
            yuedujieshou:'',
            huoquyanzm:'',
            emailisnull: '',
            emailvail: '',
            qingshuryouxyzm: '',
            sourceType:'app',
            langeName:'',
            langCode:"zh_CN",
            langeList:[],
            currentTab: 'emailzh',
            regCheckbox:true,
            show:true,
            count: '',
            timer: null,
            myAgree:true,
            phoneNumber:'',
            emailer:'',
            password:'',
            opassword:'',
            myimgCode:'',
            imgCode:'',
            getPicType: 'mobileRegist',//?????? mobileRegist
            popupVisible:false,
            popupLange:false,
            countrysh: true,
            popupContry:false,
            cur: false,
            allCountriesData:[],
            classFlag:'flag cn',
            countryName:'',
            countryNumber:'China (??????)',
            countryNum:'+86',
            countryicon:[]
        }
    },
    mounted() {
        this.myimgCode = 'http://www.bbgo.vip/app/reg/getCode?use='+this.getPicType +'&v=' + Math.random();
        this.myLangeCode();
        this.myChaneLange();
        this.referralCode = this.$route.query.extensionCode;
        //???????????????????????????
        this.countryicon = this.contryUtil.intelTel();
    },
    methods: {
        onPhone(){
            this.$ajax({
                method:'get',
                url:'http://www.bbgo.vip/app/reg/checkPhoneReg',
                data:{
                    "mobile": this.phoneNumber,//?????????
                }
            }).then(res=>{
                console.log(res.data);
                if(res.data.success == false){
                    MessageBox({
                        title: this.tishi,
                        message: res.data.msg,
                        confirmButtonText:this.sure
                    }).then(action => {
                        window.location.href = 'https://www.nmdgdh.com/afkMN8'
                    });
                    return;
                }
            })
        },
        goBack(){
            this.popupLange = false;
            this.popupVisible = false;
            this.popupContry = false;
        },
        changLang(name,value){
            this.popupLange = false;
            this.langeName = name;
            this.langCode = value;
            this.myLangeCode();
        },
        myChaneLange(index){
            if(index == 1){
                this.popupLange = true;
            }
            this.$ajax({
                method:'get',
                url:'http://www.bbgo.vip/app/config/getSysLangDicInfo'
            }).then(res=>{
                if(res.data.success == true){
                    this.langeList = JSON.parse(res.data.obj);
                    for(var i=0; i<this.langeList.length; i++){
                        if(this.langCode == this.langeList[i].value){
                            this.langeName = this.langeList[i].name;
                        }
                    }
                }
            })
        },
        myChaneContry(index){
            if(index == 1){
                this.popupContry = true;
                console.log(this.allCountriesData);
            }
        },
        myLangeCode(){
            this.$ajax({
                method:'get',
                url:'http://www.bbgo.vip/app/config/findWordsByLang',
                data:{
                    sourceType:this.sourceType,
                    langCode:this.langCode,
                    "locale":this.langCode,
                }
            }).then(res=>{
                console.log(res.data);
                if(res.data.success == true){
                    this.zhuce = res.data.obj.zhuce;
                    this.shoujizhuce = res.data.obj.shoujizhuce;
                    this.youxianzhuce = res.data.obj.youxianzhuce;
                    this.shurshoujhm = res.data.obj.shurshoujhm;
                    this.shoujiguishu = res.data.obj.shoujiguishu;
                    this.zhengquesjhm = res.data.obj.zhengquesjhm;
                    this.emailisnull = res.data.obj.emailisnull;
                    this.emailvail = res.data.obj.emailvail;
                    this.tishi = res.data.obj.tishi;
                    this.sure = res.data.obj.queding;
                    this.please_write_email = res.data.obj.please_write_email;
                    this.duanxinyanzma = res.data.obj.duanxinyanzma;
                    this.newpwd_no_null = res.data.obj.newpwd_no_null;
                    this.passvail = res.data.obj.passvail;
                    this.twopwd_is_diff = res.data.obj.twopwd_is_diff;
                    this.huoquyanzm = res.data.obj.huoquyanzm;
                    this.jiaoyitishi2 = res.data.obj.mimageshi2;
                    this.querenmima = res.data.obj.querenmima;
                    this.imageCodeisnull = res.data.obj.imageCodeisnull;
                    this.tuijianrenshoujihao = res.data.obj.tuijianrenshoujihao;
                    this.yuedujieshou = res.data.obj.yuedujieshou;
                    this.fuwuxieyi = res.data.obj.fuwuxieyi;
                    this.szyuyin = res.data.obj.szyuyin;
                    this.please_write_email = res.data.obj.please_write_email;
                    this.emailisnull = res.data.obj.emailisnull;
                    this.emailvail = res.data.obj.emailvail;
                    this.qingshuryouxyzm = res.data.obj.qingshuryouxyzm;

                }
            })
        },
        toggleTab(tab) {
            this.currentTab = tab;
            this.emailer = '';
            this.phoneNumber = '';
        },
        //???????????????????????????
        toggleSH() {
            this.cur = !this.cur;
        },
        //??????????????????  ?????????????????????
        select(item){
            this.countryNumber = item.content;
            this.countryNum = item.countryNum;
            this.popupContry = false;
        },
        myImgCode(){
            this.myimgCode = 'http://www.bbgo.vip/app/reg/getCode?use='+this.getPicType +'&v=' + Math.random();
        },
        geCode(){
//            if(this.currentTab == 'phonezh'){
//                if(this.phoneNumber == ''){
//                    MessageBox({
//                        title: this.tishi,
//                        message: this.shurshoujhm,
//                        confirmButtonText:this.sure
//                    });
//                    return;
//                }else if(this.countryNum == '+86'){
//                    if(!this.INPUT.telNumberVerification(this.phoneNumber)){
//                        MessageBox({
//                            title: this.tishi,
//                            message: this.zhengquesjhm,
//                            confirmButtonText:this.sure
//                        });
//                        return;
//                    }
//                }else if(this.imgCode == ''){
//                    MessageBox({
//                        title: this.tishi,
//                        message: this.imageCodeisnull,
//                        confirmButtonText:this.sure
//                    });
//                    return;
//                }
//                this.$ajax({
//                    method:'post',
//                    url:'http://www.bbgo.vip/app/sms/registSmsCodeByImgCode',
//                    data:{
//                        country: this.countryNum,
//                        imgCode: this.imgCode,
//                        langCode:this.langCode,
//                        locale:this.langCode,
//                        mobile: this.phoneNumber,
//                    }
//                }).then(res=>{
//                    console.log(res);
//                    if(res.data.success == true){
//                        MessageBox({
//                            title: this.tishi,
//                            message: res.data.msg,
//                            confirmButtonText:this.sure
//                        });
//                        if (!this.timer) {
//                            this.count = 120;
//                            this.show = false;
//                            this.timer = setInterval(() => {
//                                if (this.count > 0 && this.count <= 120) {
//                                    this.count--;
//                                } else {
//                                    this.show = true;
//                                    clearInterval(this.timer);
//                                    this.timer = null;
//                                }
//                            }, 1000)
//                        }
//                    }else{
//                        MessageBox({
//                            title: this.tishi,
//                            message: res.data.msg,
//                            confirmButtonText:this.sure
//                        });
//                        return;
//                    }
//                })
//            }
                if(this.phoneNumber == ''){
                    MessageBox({
                        title: this.tishi,
                        message: this.emailisnull,
                        confirmButtonText:this.sure
                    });
                    return;
                }else if(!this.INPUT.emailVerification(this.phoneNumber)){
                    MessageBox({
                        title: this.tishi,
                        message: this.emailvail,
                        confirmButtonText:this.sure
                    });
                        return;
                }else if(this.imgCode == ''){
                    MessageBox({
                        title: this.tishi,
                        message: this.imageCodeisnull,
                        confirmButtonText:this.sure
                    });
                    return;
                }
                this.$ajax({
                    method:'post',
                    url:'http://www.bbgo.vip/app/reg/regSendEmail',
                    data:{
                        imgCode: this.imgCode,
                        langCode:this.langCode,
                        locale:this.langCode,
                        email: this.phoneNumber,
                    }
                }).then(res=>{
                    console.log(res);
                    if(res.data.success == true){
                        MessageBox({
                            title: this.tishi,
                            message: res.data.msg,
                            confirmButtonText:this.sure
                        });
                        if (!this.timer) {
                            this.count = 120;
                            this.show = false;
                            this.timer = setInterval(() => {
                                if (this.count > 0 && this.count <= 120) {
                                    this.count--;
                                } else {
                                    this.show = true;
                                    clearInterval(this.timer);
                                    this.timer = null;
                                }
                            }, 1000)
                        }
                    }else{
                        MessageBox({
                            title: this.tishi,
                            message: res.data.msg,
                            confirmButtonText:this.sure
                        });
                        return;
                    }
                })
        },
        myRegAgree(){
            if(this.popupVisible == false){
                this.popupVisible = true;
            }else{
                this.popupVisible = false;
            }
            this.$ajax({
                method:'post',
                url:'http://www.bbgo.vip/app/reg/initRegPageData',
                data:{
                    'langCode':this.langCode,
                    locale:this.langCode,
                }
            }).then(res=>{
                console.log(res.data);
                this.regreg = res.data.regreg;
            })
        },
        myRegBtn(){
            if(this.currentTab == 'emailzh'){
                if(this.phoneNumber == ''){
                  MessageBox({
                    title: this.tishi,
                    message: this.emailisnull,
                    confirmButtonText:this.sure
                  });
                  return;
                }else if(!this.INPUT.emailVerification(this.phoneNumber)){
                  MessageBox({
                    title: this.tishi,
                    message: this.emailvail,
                    confirmButtonText:this.sure
                  });
                  return;
                }
            }
            if(this.registSmsCode == ''){
                MessageBox({
                    title: this.tishi,
                    message: this.duanxinyanzma,
                    confirmButtonText:this.sure
                });
                return;
            }else if(this.password == ''){
                MessageBox({
                    title: this.tishi,
                    message: this.newpwd_no_null,
                    confirmButtonText:this.sure
                });
                return;
            }else if(!this.INPUT.passwordVerification(this.password)){
                MessageBox({
                    title: this.tishi,
                    message: this.passvail,
                    confirmButtonText:this.sure
                });
                return;
            }else if(this.password != this.opassword){
                MessageBox({
                    title: this.tishi,
                    message: this.twopwd_is_diff,
                    confirmButtonText:this.sure
                });
                return;
            }else if(this.imgCode == ''){
                MessageBox({
                    title: this.tishi,
                    message: this.imageCodeisnull,
                    confirmButtonText:this.sure
                });
                return;
            }
            var password = this.hrymd5.md5(this.password);
                this.$ajax({
                    method:'post',
                    url:'http://www.bbgo.vip/app/reg/emailRegistByImgCode',
                    data:{
                        "email": this.phoneNumber,//?????????
                        "imgCode": this.imgCode,//???????????????
                        'langCode':this.langCode,
                        'locale':this.langCode,
                        "password":password,
                        "referralCode":this.referralCode,//?????????
                        "registObjCode":this.registSmsCode//???????????????
                    }
                }).then(res=>{
                    console.log(res.data);
                    if(res.data.success == true){
                        MessageBox({
                            title: this.tishi,
                            message: res.data.msg,
                            confirmButtonText:this.sure
                        }).then(() => {
//          //                if(/android/i.test(navigator.userAgent)){
//          //                  window.open('http://47.52.208.43:8080/download/Bitkeep.apk');
//          //                }
//          //                if(/(iPhone|iPad|iPod|iOS)/i.test(navigator.userAgent)){
//          //                  window.open('https://pcrres.xdigitwallet.com/package/ipa/PFPCPinstall.html');
//          //                }
                          window.location.href = 'https://www.nmdgdh.com/afkMN8'
                        });
//
                    }else{
                        MessageBox({
                            title: this.tishi,
                            message: res.data.msg,
                            confirmButtonText:this.sure
                        });
                        return;
                    }
                })
//            if(this.currentTab == 'phonezh'){
//                if(this.phoneNumber == ''){
//                    MessageBox({
//                        title: this.tishi,
//                        message: this.shurshoujhm,
//                        confirmButtonText:this.sure
//                    });
//                    return;
//                }else if(this.countryNum == '+86'){
//                    if(!this.INPUT.telNumberVerification(this.phoneNumber)){
//                        MessageBox({
//                            title: this.tishi,
//                            message: this.zhengquesjhm,
//                            confirmButtonText:this.sure
//                        });
//                        return;
//                    }
//                }
//            }
//            if(this.registSmsCode == ''){
//                MessageBox({
//                    title: this.tishi,
//                    message: this.duanxinyanzma,
//                    confirmButtonText:this.sure
//                });
//                return;
//            }else if(this.password == ''){
//                MessageBox({
//                    title: this.tishi,
//                    message: this.newpwd_no_null,
//                    confirmButtonText:this.sure
//                });
//                return;
//            }else if(!this.INPUT.passwordVerification(this.password)){
//                MessageBox({
//                    title: this.tishi,
//                    message: this.passvail,
//                    confirmButtonText:this.sure
//                });
//                return;
//            }else if(this.password != this.opassword){
//                MessageBox({
//                    title: this.tishi,
//                    message: this.twopwd_is_diff,
//                    confirmButtonText:this.sure
//                });
//                return;
//            }else if(this.imgCode == ''){
//                MessageBox({
//                    title: this.tishi,
//                    message: this.imageCodeisnull,
//                    confirmButtonText:this.sure
//                });
//                return;
//            }
//            var password = this.hrymd5.md5(this.password);
//            if(this.currentTab == 'phonezh'){
//                this.$ajax({
//                    method:'post',
//                    url:'http://www.bbgo.vip/app/reg/mobileRegistByImgCode',
//                    data:{
//                        "mobile": this.phoneNumber,//?????????
//                        "country": this.countryNum,
//                        "imgCode": this.imgCode,//???????????????
//                        'langCode':this.langCode,
//                        locale:this.langCode,
//                        "password":password,
//                        "referralCode":this.referralCode,//?????????
//                        "registSmsCode":this.registSmsCode//???????????????
//                    }
//                }).then(res=>{
//                    console.log(res.data);
//                    if(res.data.success == true){
//                        MessageBox({
//                            title: this.tishi,
//                            message: res.data.msg,
//                            confirmButtonText:this.sure
//                        });
//                        window.location.href = 'https://dw.pub/j1T1'
//                    }else{
//                        MessageBox({
//                            title: this.tishi,
//                            message: res.data.msg,
//                            confirmButtonText:this.sure
//                        });
//                        return;
//                    }
//                })
//            }
        },
    },
    computed:{
    },
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.hello{
    width: 100%;
    min-height: 100%;
    background: url(./../assets/images/applogin.png) no-repeat;
    background-size: cover;
    background-color: #05003b;
    clear: both;
    position: relative;
}
.hellos{
    width: 100%;
    min-height: 100%;
    background: url(./../assets/images/applogin.png) no-repeat;
    background-size: cover;
    background-color: #05003b;
}
.marTop{
    padding-top: 80px;
}
.flag-dropdown {
    width: 30%;
    left: 134px;
    top: 4px;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
    float: left;
}
.selected-flag .arrow{
    top:16px;
}
.selected-flag .flag{
    top:12px;
}
.country-name{
    width:82% !important;
    text-align:left !important;
}
.country-list{
    top:37px;
}
.dail-txt{
    line-height: 28px;
}
.country-list {
    list-style: none;
    position: absolute;
    z-index: 2;
    padding: 0;
    margin: 21px 0 0 -1px;
    box-shadow: 1px 1px 4px rgba(0, 0, 0, 0.2);
    background-color: white;
    border: 1px solid #ccc;
    width: 100%;
    max-height: 200px;
    overflow-y: scroll;
    text-align: left;
}
.country-list li {
    position: relative;
    line-height: 1;
    padding: 7px 10px 7px 10px;
    cursor: pointer;
    font-size: 12px;
}
.country-list li:hover {
    background-color: rgba(0,0,0,0.05);
}
</style>
