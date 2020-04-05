function onDelete(id) {
    const xhr = new XMLHttpRequest();
    xhr.open('DELETE', `web/delete/${id}`);
    xhr.send();
    xhr.onload = () => {
        if(xhr.status !== 200){
            alert(`Ошибка ${xhr.status}: ${xhr.statusText}`);
        } else {
            document.location.reload();
        }
    };
    xhr.onerror = function() {
        alert("Запрос не удался");
    };
}

document.addEventListener("DOMContentLoaded", event => {
    const foods = document.getElementsByClassName("food_row");
    const updateForm = document.getElementById("update_form");
    const createForm = document.getElementById("create_form");
    const addFoodBtn = document.getElementById("add_food_btn");
    const updateFoodBtn = document.getElementById("update_food_btn");
    const closeUpdateBtn = document.getElementById("close_update_btn");

    for(const food of foods){
        food.addEventListener("click", () => {
            const [id, name, type, calories] = Array.from(food.cells).map(x => x.textContent);
            createForm.style.display = "none";
            addFoodBtn.disabled = true;
            updateFoodBtn.disabled = false;
            closeUpdateBtn.disabled = false;
            updateForm.style.display = "block";
            updateForm.elements['id'].value = id
            updateForm.elements['name'].value = name
            updateForm.elements['type'].value = type
            updateForm.elements['calories'].value = calories
        })
    }

    document.getElementById("close_update_btn").addEventListener("click", () => {
        updateFoodBtn.disabled = true;
        updateForm.style.display = "none";
        createForm.style.display = "block";
        addFoodBtn.disabled = false;
        closeUpdateBtn.disabled = true;
    })
})