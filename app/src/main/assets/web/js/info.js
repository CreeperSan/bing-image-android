let isLicenseOpen = false;
let isUpdateOpen = false;

const ID_LICENSE_ICON = "licenseIcon";
const ID_LICENSE_GROUP = "licenseGroup";

const ID_UPDATE_ICON = "updateIcon";
const ID_UPDATE_GROUP = "updateGroup";

const ID_APP_NAME = "appName";
const ID_APP_VERSION = "appVersion";

const elementLicenseIcon = document.getElementById(ID_LICENSE_ICON);
const elementLicenseGroup = document.getElementById(ID_LICENSE_GROUP);
const elementUpdateIcon = document.getElementById(ID_UPDATE_ICON);
const elementUpdateGroup = document.getElementById(ID_UPDATE_GROUP);
const elementAppName = document.getElementById(ID_APP_NAME);
const elementAppVersion = document.getElementById(ID_APP_VERSION);

function onLicenseClick() {
    if (isLicenseOpen){
        elementLicenseIcon.setAttribute("class","listItemTitleIconOpen");
        elementLicenseGroup.style.display = "none";
    }else{
        elementLicenseIcon.setAttribute("class","listItemTitleIconClose");
        elementLicenseGroup.style.display = "flex";
    }
    isLicenseOpen = !isLicenseOpen;
}

function onUpdateClick() {
    if (isUpdateOpen){
        elementUpdateIcon.setAttribute("class","listItemTitleIconOpen");
        elementUpdateGroup.style.display = "none";
    }else{
        elementUpdateIcon.setAttribute("class","listItemTitleIconClose");
        elementUpdateGroup.style.display = "flex";
    }
    isUpdateOpen = !isUpdateOpen;
}

function setAppName(appName) {
    elementAppName.innerText = appName;
}

function setAppVersion(version) {
    elementAppVersion.innerText = version
}
