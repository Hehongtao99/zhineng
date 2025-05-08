<template>
  <div>
    <!-- <img :src="login_picture" class="login-pic"> -->
    <el-card class="login-form-layout">
      <el-form v-if="type=='login'" autoComplete="on"
               :model="loginForm"
               :rules="loginRules"
               ref="loginForm"
               label-position="left">
        <div style="text-align: center">
          <svg-icon icon-class="user" style="width: 56px;height: 56px;color: #ed6969"></svg-icon>
        </div>
        <h2 class="login-title color-main">智能企业办公日程规划系统</h2>
        <h2 class="login-title color-main">用户登录</h2>
        <el-form-item prop="username">
          <el-input name="username"
                    type="text"
                    v-model="loginForm.username"
                    autoComplete="on"
                    placeholder="请输入用户名">
          <span slot="prefix">
            <svg-icon icon-class="user" class="color-main"></svg-icon>
          </span>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input name="password"
                    :type="pwdType"
                    @keyup.enter.native="handleLogin"
                    v-model="loginForm.password"
                    autoComplete="on"
                    placeholder="请输入密码">
          <span slot="prefix">
            <svg-icon icon-class="password" class="color-main"></svg-icon>
          </span>
            <span slot="suffix" @click="showPwd">
            <svg-icon icon-class="eye" class="color-main"></svg-icon>
          </span>
          </el-input>
        </el-form-item>
        <el-form-item style="margin-bottom: 60px;text-align: center">
          <el-button style="width: 45%" type="danger" :loading="loading" @click.native.prevent="handleLogin">
            登录
          </el-button>
          <el-button style="width: 45%" type="danger" @click.native.prevent="handleToRegister">
            注册
          </el-button>
        </el-form-item>
      </el-form>
      <el-form v-if="type=='register'" autoComplete="on"
               :model="registerForm"
               :rules="registerRules"
               ref="registerForm"
               label-position="left">
        <div style="text-align: center">
          <svg-icon icon-class="user" style="width: 56px;height: 56px;color: #ed6969"></svg-icon>
        </div>
        <h2 class="login-title color-main">智能企业办公日程规划系统</h2>
        <h2 class="login-title color-main">用户注册</h2>
        <el-form-item prop="username">
          <el-input name="username"
                    type="text"
                    v-model="registerForm.username"
                    autoComplete="on"
                    placeholder="请输入用户名">
          <span slot="prefix">
            <svg-icon icon-class="user" class="color-main"></svg-icon>
          </span>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input name="password"
                    :type="regPwdType"
                    @keyup.enter.native="handleLogin"
                    v-model="registerForm.password"
                    autoComplete="on"
                    placeholder="请输入密码">
          <span slot="prefix">
            <svg-icon icon-class="password" class="color-main"></svg-icon>
          </span>
            <span slot="suffix" @click="showRegPwd">
            <svg-icon icon-class="eye" class="color-main"></svg-icon>
          </span>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input name="password"
                    :type="regConfirmPwdType"
                    v-model="registerForm.confirmPassword"
                    autoComplete="on"
                    placeholder="请确认密码">
          <span slot="prefix">
            <svg-icon icon-class="password" class="color-main"></svg-icon>
          </span>
            <span slot="suffix" @click="showConfirmPwd">
            <svg-icon icon-class="eye" class="color-main"></svg-icon>
          </span>
          </el-input>
        </el-form-item>
        <el-form-item style="margin-bottom: 60px;text-align: center">
          <el-button style="width: 45%" type="danger" @click.native.prevent="handleToLogin">
            已有账号去登录
          </el-button>
          <el-button style="width: 45%" type="danger" :loading="regLoading" @click.native.prevent="handleRegister">
            提交注册
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <img :src="login_picture" class="login-center-layout">
  </div>
</template>

<script>
  import {isvalidUsername,} from '@/utils/validate';
  import {setCookie,getCookie} from '@/utils/support';
  import login_center_bg from '@/assets/images/login_center_bg.png'
  import login_picture from '@/assets/images/login_picture.png'
  import { createAdmin } from '@/api/login';

  export default {
    name: 'login',
    data() {
      const validateUsername = (rule, value, callback) => {
        if (!isvalidUsername(value)) {
          callback(new Error('请输入正确的用户名'))
        } else {
          callback()
        }
      };
      const validatePass = (rule, value, callback) => {
        if (value.length < 3) {
          callback(new Error('密码不能小于3位'))
        } else {
          callback()
        }
      };
      return {
        loginForm: {
          username: '',
          password: '',
        },
        registerForm: {
          username: '',
          password: '',
          confirmPassword: '',
        },
        loginRules: {
          username: [{required: true, trigger: 'blur', validator: validateUsername}],
          password: [{required: true, trigger: 'blur', validator: validatePass}]
        },
        registerRules: {
          username: [{required: true, trigger: 'blur', validator: validateUsername}],
          password: [{required: true, trigger: 'blur', validator: validatePass}]
        },
        loading: false,
        regLoading: false,
        pwdType: 'password',
        regPwdType: 'password',
        regConfirmPwdType: 'password',
        login_center_bg,
        login_picture,
        type: 'login' // login/register
      }
    },
    created() {
      this.loginForm.username = getCookie("username");
      this.loginForm.password = getCookie("password");
      if(this.loginForm.username === undefined||this.loginForm.username==null||this.loginForm.username===''){
        this.loginForm.username = '';
      }
      if(this.loginForm.password === undefined||this.loginForm.password==null){
        this.loginForm.password = '';
      }
    },
    methods: {
      showPwd() {
        if (this.pwdType === 'password') {
          this.pwdType = ''
        } else {
          this.pwdType = 'password'
        }
      },
      showRegPwd() {
        if (this.regPwdType === 'password') {
          this.regPwdType = ''
        } else {
          this.regPwdType = 'password'
        }
      },
      showConfirmPwd() {
        if (this.regConfirmPwdType === 'password') {
          this.regConfirmPwdType = ''
        } else {
          this.regConfirmPwdType = 'password'
        }
      },
      handleLogin() {
        this.$refs.loginForm.validate(valid => {
          if (valid) {
            this.loading = true;
            this.$store.dispatch('Login', this.loginForm).then(() => {
              this.loading = false;
              setCookie("username",this.loginForm.username,15);
              setCookie("password",this.loginForm.password,15);
              this.$router.push({path: '/'})
            }).catch(() => {
              this.loading = false
            })
          } else {
            console.log('参数验证不合法！');
            return false
          }
        })
      },
      handleRegister() {
        this.$refs.registerForm.validate(valid => {
          if (valid) {
            this.regLoading = true;
            if (this.registerForm.password !== this.registerForm.confirmPassword) {
              this.$message({
                message: '两次输入密码不一致！',
                type: 'error'
              });
              this.regLoading = false;
              return false;
            }
            createAdmin(this.registerForm).then(response => {
              this.$message({
                message: '注册成功！',
                type: 'success'
              });
              this.type = 'login';
              this.loginForm.username = this.registerForm.username;
              this.regLoading = false;
            });
          } else {
            console.log('参数验证不合法！');
            return false
          }
        })
      },
      handleToLogin() {
        this.type = 'login';
      },
      handleToRegister() {
        this.type = 'register';
      },
    }
  }
</script>

<style scoped>
  .login-pic {
    position: absolute;
    width: 60%;
    margin: 140px auto;
    left: 100px;
  }

  .login-form-layout {
    position: absolute;
    /* left: 0;
    right: 0;
    margin: 140px auto; */
    margin: 140px 0;
    right: 100px;
    width: 360px;
    border-top: 10px solid #ed6969;
  }

  .login-title {
    text-align: center;
  }

  .login-center-layout {
    background: #ed6969;
    width: 100%;
    height: 100%;
    max-width: 100%;
    max-height: 100%;
    background-size: contain;
    /* margin-top: 200px; */
  }
</style>
