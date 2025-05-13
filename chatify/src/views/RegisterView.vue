<script>
import axios from "axios";
    export default {
        data() {
            return {
                username: "",
                password: "",
                error: false
            }
        },
        methods: {
            async register(evt) {
                evt.preventDefault();
                
                try {
                let {publicKey, privateKey} = this.generateKeypair();
                    let response = await axios.post("api/register", {
                        username: this.username,
                        password: this.password,
                        publicKey: JSON.stringify(publicKey)
                        }, 
                        {
                        headers: {
                            'Content-Type': 'application/json',
                        }}
                );
                console.log(response.data);
                // генерация ключей
                response = await axios.post("api/private_keys", {
                      username: this.username,
                      privateKey: JSON.stringify(privateKey)
                    },
                    {
                      headers: {
                        'Content-Type': 'application/json',
                      }});
                localStorage.setItem("privateKey", JSON.stringify(privateKey) );

                this.$router.push("/login", {});
                } catch(e) {
                    this.error = true;
                    console.log(e);
                }
            },
          generateRandomPrime(min, max) {
  // Генерация случайного простого числа в диапазоне [min, max]
  function isPrime(num) {
    if (num <= 1) return false;
    if (num <= 3) return true;
    if (num % 2 === 0 || num % 3 === 0) return false;
    let i = 5;
    while (i * i <= num) {
      if (num % i === 0 || num % (i + 2) === 0) return false;
      i += 6;
    }
    return true;
  }

  let prime;
  do {
    prime = Math.floor(Math.random() * (max - min + 1)) + min;
  } while (!isPrime(prime));

  return prime;
},

 getCoprime(phi) {
  // Выбор случайного числа e, взаимно простого с phi
  function gcd(a, b) {
    while (b !== 0) {
      [a, b] = [b, a % b];
    }
    return a;
  }

  let e;
  do {
    e = Math.floor(Math.random() * (phi - 2)) + 2; // 1 < e < phi
  } while (gcd(e, phi) !== 1);

  return e;
},

 generateKeypair() {
  // Генерация случайных простых чисел
  const minPrime = 1000, maxPrime = 10000; // Можно увеличить диапазон для большей безопасности
  let p = this.generateRandomPrime(minPrime, maxPrime);
  let q = this.generateRandomPrime(minPrime, maxPrime);

  // Убедимся, что p и q разные
  while (q === p) {
    q = this.generateRandomPrime(minPrime, maxPrime);
  }

  const n = p * q;

  // Функция Эйлера
  const phi = (p - 1) * (q - 1);

  // Выбор случайной открытой экспоненты e
  const e = this.getCoprime(phi);

  // Вычисление секретной экспоненты d
  const d = this.modInverse(e, phi);

  // Публичный ключ (e, n)
  const publicKey = { e, n };
  // Приватный ключ (d, n)
  const privateKey = { d, n };

  return { publicKey, privateKey };
},

// Функция для нахождения модульного обратного числа
        modInverse(a, m) {
            // Расширенный алгоритм Евклида
            let [old_r, r] = [a, m];
            let [old_s, s] = [1, 0];
            
            while (r !== 0) {
                const quotient = Math.floor(old_r / r);
                [old_r, r] = [r, old_r - quotient * r];
                [old_s, s] = [s, old_s - quotient * s];
            }
            
            // Убедимся, что результат положительный
            return (old_s < 0) ? old_s + m : old_s;
        },
        // Функция быстрого возведения в степень по модулю
        modPow(base, exponent, modulus) {
            if (modulus === 1) return 0;
            
            let result = 1;
            base = base % modulus;
            
            while (exponent > 0) {
                if (exponent % 2 === 1) {
                    result = (result * base) % modulus;
                }
                exponent = Math.floor(exponent / 2);
                base = (base * base) % modulus;
            }
            
            return result;
        }}
    }
</script>
<template>
 <div class="container">
    <div class="login">
        <div class="login__title">Регистрация</div>
        <form action="#" class="login__form">
            <input v-model="username" type="text" name="username" placeholder="имя">
            <input v-model="password" type="password" name="password" placeholder="пароль">
            <div  v-if="error"class="form__error">такой пользователь уже есть</div>
            <button @click="register" type="submit">Зарегестрироваться</button>
        </form>
    </div>
 </div>
</template>
<style scoped>
.container {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100vh;
}

.login {
    border-radius: 20px;    
    padding: 40px;
    flex: 0 1 40%;
    background: #fff;
}

.login__title {
    font-size: 40px;
    text-transform: uppercase;
    text-align: center;
    color: #000;
    margin-bottom: 20px;
}

.login__form {
    text-align: center;
    margin-bottom: 20px;
}

.login__form input {
    display: block;
    width: 100%;
    margin-bottom: 20px;
    font-size: 20px;
    line-height: 2;
    padding-left: 10px;
    border: 2px solid #000;
}

.login__form input:focus {
    outline: 0;
}

.login__form button {
    background: #387f92;
    padding: 0 70px;
    font-size: 26px;
    line-height: 2;
    border: none;
    color: #fff;
    cursor: pointer;
    transition-duration: 100ms;
}

.login__form button:hover {
    background: #fff;
    color: #387f92;
}

.form__error {
    font-size: 18px;
    margin-bottom: 20px;
    color: red;

}

.login {
    text-align: center;
}

.login a {
    text-decoration: none;
}

</style>