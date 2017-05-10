
class BaseService {

    listeners = [];

    constructor() {
    }

    subscribe(onChange) {
        this.listeners.push(onChange);
    }

    notify() {
        this.listeners.forEach(it => it());
    }


}

export default BaseService;