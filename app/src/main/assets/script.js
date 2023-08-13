// Get all the dots from the document
let dots = document.querySelectorAll(".dot");

// We will be adding a delay of 0.2 between animation associated with each dot
let delay = 0;

//Function that will animate our dots. It takes the element and the delay.
const animate = (el, delay) => {
    gsap.to(
        el,
        {
            ease: Power1.easeInOut, //Easing
            translateY: -30, //Move the dot -30px on Y axis
            yoyo: true, // If true, playback will alternate forwards and backwards on each repeat
            repeat: -1, // How many times the animation should repeat (-1 is infinite)
            duration: 1, // the speed of the animation
            delay: delay, // time after which the animation starts
        },
        0
    );
};

//Iterate over each dot and run the animate method, and also increment the delay by '.2s'
dots.forEach((dot) => {
    animate(dot, delay);
    delay += 0.2;
});