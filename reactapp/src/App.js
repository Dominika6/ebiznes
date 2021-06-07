// import './App.css';
// import React from 'react';
// import './App.css';
// import { Route } from "react-router-dom";
// import Welcome from "./components/Welcome";
// import {BrowserRouter, Switch} from "react-router-dom";
// import Movie from "./components/Movie";
// import UserContextProvider from "./components/UserContextProvider";
// import FilmtypeMovies from "./components/FilmtypeMovies";
// import UserLibrary from "./components/UserLibrary";
// import ActorMovies from "./components/ActorMovies";
// import BasketContextProvider from "./components/BasketContextProvider";
// import Basket from "./components/Basket";
// import UserProfile from "./components/UserProfile";
// import DirectorMovies from "./components/DirectorMovies";
// import AllMovies from "./components/AllMovies";
// import NavigationBar from "./components/NavigationBar";
// import Footer from "./components/Footer";
//
// function App() {
//
//   return (
//       <UserContextProvider>
//           <BasketContextProvider>
//               <BrowserRouter>
//                   <NavigationBar/>
//                   <Switch>
//                       <Route path="/home" exact component={Welcome}/>
//                       <Route path="/" component={AllMovies} />
//                       <Route path="/gatunek/:filmtypeName" component={FilmtypeMovies} />
//                       <Route path="/filmy/:movieId" component={Movie} />
//                       <Route path="/aktor/:actorId" component={ActorMovies}/>
//                       <Route path="/rezyser/:directorId" component={DirectorMovies} />
//                       <Route path="/biblioteka" component={UserLibrary} />
//                       <Route path="/koszyk" component={Basket} />
//                       <Route path="/profil" component={UserProfile} />
//                   </Switch>
//               </BrowserRouter><br/><br/><br/>
//               <Footer/>
//           </BasketContextProvider>
//       </UserContextProvider>
//   );
// }
//
// export default App;
