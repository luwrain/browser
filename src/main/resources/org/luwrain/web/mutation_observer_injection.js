function mutationCallback(mutationsList, observer) {
    webKitBlocks.setNeedsToBeUpdated(true);
}

const observer = new MutationObserver(mutationCallback);

const target = document.body

const config = {
    subtree: true,
    childList: true,
    attributes: true
};

observer.observe(target, config);