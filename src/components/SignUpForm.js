import React from 'react';
import { StyleSheet, View, TextInput, Text, TouchableOpacity } from 'react-native';
import { Redirect } from 'react-router-native';
// import { API_BASE_URL } from '../config';
export default class SignUpForm extends React.Component {

    constructor(props){
        super(props)
    
        this.state = {
          username: '',
          password: '',
          confirmPassword: '',
          pin: '',
          signupFailed: false,
          signupPassError: false,
          signUpComplete: false,
          pressed: false
        }
      }
    handlePress = () => {
        this.setState({signupPassError:false, signupFailed: false, pressed: true});
        if(this.state.password === this.state.confirmPassword) {
            //dispatch signup
            const userData = {
                username : this.state.username,
                password : this.state.password,
                pin : this.state.pin
            }
            this.handleSignUp(userData);
            console.log("password and confirm matches");
        }
        else {
            this.setState({signupPassError:true});
            //view of inccorect pass
            console.log("no match");
        }
        console.log('pressed');
    }
    handleLogin = (userData) => {
        console.log(userData);
        console.log({username: userData.username, password: userData.password});
        fetch(`https://safedeliver.herokuapp.com/api/users/login`,
        {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                username: userData.username,
                password: userData.password
            })
        })
        .then(res => {
            if (!res.ok) {
                return Promise.reject(res.statusText);
            }
            // login here
            console.log('logged in');
            this.setState({signUpComplete: true});
        })
        .catch(err => console.log(err))
    }
    handleSignUp = (userData) => {
        // console.log(userData);
        // if signup fails we set state of signupfailed to true
        fetch(`https://safedeliver.herokuapp.com/api/users/signup`,
        {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(userData)
        })
        .then(res => {
            if (!res.ok) {
                this.setState({signupFailed:true});
                return Promise.reject(res.statusText);
            }
            // dispatch(login(data)) - we login here once everything is GUCCIGANGGGG
            console.log('successfully created account');
            // this.setState({signUpComplete: true});
            this.handleLogin(userData);
        })
        .catch(err => console.log(err))
    }
    render() {
        //turn off to test
        //if signup compleete is true and pressed then we redirect.
        if(this.state.pressed && this.state.signUpComplete) {
            return <Redirect to="/HomePage" />
        }
        return (
            <View style={styles.container}>
                <TextInput
                    placeholder="username"
                    placeholderTextColor="rgba(255,255,255,0.8)"
                    underlineColorAndroid={'transparent'}
                    returnKeyType="next"
                    onSubmitEditing={() => this.secondTextInput.focus()}
                    blurOnSubmit={false}
                    onChangeText={(text) => this.setState({username:text})}
                    style={styles.input}>
                </TextInput>
                <TextInput
                    placeholder="pin"
                    placeholderTextColor="rgba(255,255,255,0.8)"
                    underlineColorAndroid={'transparent'}
                    returnKeyType="next"
                    ref={(input) => this.secondTextInput = input}
                    onSubmitEditing={() => this.thirdTextInput.focus()}
                    blurOnSubmit={false}
                    onChangeText={(text) => this.setState({pin:text})}
                    style={styles.input}>
                </TextInput>
                <TextInput
                    placeholder="password"
                    placeholderTextColor="rgba(255,255,255,0.8)"
                    secureTextEntry
                    underlineColorAndroid={'transparent'}
                    returnKeyType="next"
                    ref={(input) => this.thirdTextInput = input}
                    onSubmitEditing={() => this.fourthTextInput.focus()}
                    blurOnSubmit={false}
                    onChangeText={(text) => this.setState({password:text})}
                    style={styles.input}>
                </TextInput>
                <TextInput
                    placeholder="confirm password"
                    placeholderTextColor="rgba(255,255,255,0.8)"
                    secureTextEntry
                    underlineColorAndroid={'transparent'}
                    returnKeyType="go"
                    ref={(input) => this.fourthTextInput = input}
                    onChangeText={(text) => this.setState({confirmPassword:text})}
                    style={styles.input}>
                </TextInput>
                <TouchableOpacity onPress={this.handlePress}style={styles.buttonContainer}>
                    <Text style={styles.buttonText}>
                        {/* request to login here */}
                        Sign Up
                    </Text>
                </TouchableOpacity>
                <Text> 
                {this.state.signupPassError && <Text> invalid pw </Text> 
                    || this.state.signupFailed && <Text> failed signup </Text>
                }
                </Text>
            </View>
        )
    }
}

const styles = StyleSheet.create({
    container: {
        padding: 20
    },
    input: {
        height: 40,
        backgroundColor: 'rgba(255,255,255,0.2)',
        marginBottom: 10,
        color: '#FFF',
        paddingHorizontal: 10
    },
    buttonContainer: {
        backgroundColor: '#3F51B5',
        paddingVertical: 15
    },
    buttonText: {
        textAlign: 'center',
        color: '#FFFFFF',
        fontWeight: '700'
    }
})