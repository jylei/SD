import React from 'react';
import { StyleSheet, View, Image, Text, Button } from 'react-native';
import { Redirect } from 'react-router-native';
import LoginForm from './LoginForm';
import SignUp from './SignUp';

export default class Login extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            display: 'loginForm'
        }; //this is how  you set up state
    }

    signUpForm = () => {
        console.log('signup');
        this.setState({ display: 'SignUpForm' });
    }

    render() {
        if (this.state.display === 'SignUpForm') {
            console.log('here');
            return <Redirect to='/SignUp'/>;
        }
        return (
            <View style={styles.container}>

                <View style={styles.logoContainer}>
                    <Text style={styles.textLogo}>
                        LOGO
                    </Text>
                    <Text style={styles.title}>
                        Login text
                    </Text>
                </View>
                <View style={styles.formContainer}>
                    <LoginForm handleForm={this.signUpForm} />
                </View>
            </View>
        )
    }
}

// current toggleswitch turns on the floating button

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: '#2196F3'
    },
    logoContainer: {
        alignItems: 'center',
        flexGrow: 0.5,
        justifyContent: 'center'
    },
    logo: {
        width: 100,
        height: 100
    },
    textLogo: {
        fontSize: 30,
        fontWeight: 'bold'
    },
    title: {
        color: '#FFF',
        marginTop: 10,
        width: 100,
        opacity: 0.9,
        textAlign: 'center',
        fontSize: 20
    }
});