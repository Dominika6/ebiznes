import {createMuiTheme} from '@material-ui/core/styles';

const theme = createMuiTheme({
    overrides: {
        MuiListItem: {
            root: {
                '&.active': {
                    backgroundColor: '#252340'
                },
                '&.active:hover': {
                    backgroundColor: '#7689a7'
                }
            }
        }
    },
    typography: {
        fontFamily: [
            'Arial'
        ],
        h5: {
            fontWeight: "bold"
        }
    },
    palette: {
        primary: {
            main: '#252340',
        },
        secondary: {
            main: '#252340',
        },
        text: {
            primary: '#fcf7e7'
        },
        background: {
            paper: '#252340',
            default: '#fcf7e7'
        },
    },
});

// theme.shadows = [];
export default theme;