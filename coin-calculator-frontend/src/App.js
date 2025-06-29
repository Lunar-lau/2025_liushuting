import React, {useState} from 'react';
import './App.css';

const COINS_OPTIONS = ["0.01", "0.05", "0.1", "0.2", "0.5","1", "2", "5", "10", "50", "100", "1000"];

function App() {
  const [amount, setAmount] = useState('');
  const [selectedCoins, setSelectedCoins] = useState(["0.01"]);
  const [message, setMessage] = useState('');

  const handleCoinsChange = (event) => {
    const value = event.target.value;
    setSelectedCoins(prev => {
      let newCoins;
      if (event.target.checked) {
        newCoins = [...prev, value];
      } else {
        newCoins = prev.filter(coin => coin !== value);
      }
      return newCoins.sort((a, b) => Number(a) - Number(b));
    });
  };

  const handleSubmit = async () => {
    if (!amount) {
      setMessage("Please enter an amount.");
      return;
    }

    if (selectedCoins.length === 0){
      setMessage("Please select at least one coin denomination.");
      return;
    }

    try {
      const coinsParam = selectedCoins.join(',');
      const response = await fetch(`/api/coins/${amount}?coins=${coinsParam}`);
      const data = await response.json();
      setMessage(data.message)
    } catch (error) {
      console.error('Error:', error);
      setMessage('Backend API request failed, please ensure the Dropwizard backend is running.');
    }
  };

  return(
    <div className="app-container">
      <h2 className="app-title">Coin Combination</h2>
      <p>Please enter an amount within the range between 0 and 10,000.00</p>
      <input 
      className="app-input"
      type='number' 
      placeholder='Please Enter your amount' 
      value={amount} 
      onChange={(e) => setAmount(e.target.value)}/>

      <p>Please select coin denominations</p>
      <div className='checkbox-group'>
        {COINS_OPTIONS.map(coin => (
          <label key={coin} className='checkbox-label'>
            <input
            type='checkbox'
            value={coin}
            checked={selectedCoins.includes(coin)}
            onChange={handleCoinsChange}/>
            {coin}
          </label>
        ))}
      </div>
      
      <button className="app-button" onClick={handleSubmit}>Submit</button>

      <div className="app-output">
        {message}
      </div>
    </div>
  );
}

export default App;